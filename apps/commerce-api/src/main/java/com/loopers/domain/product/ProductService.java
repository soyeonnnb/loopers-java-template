package com.loopers.domain.product;

import com.loopers.application.product.ProductCacheDto;
import com.loopers.application.product.ProductCacheDtoList;
import com.loopers.application.product.ProductSortOrder;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    public Optional<ProductEntity> getProductInfo(Long productId) {
        return productRepository.findById(productId);
    }

    @Transactional(readOnly = true)
    public ProductCacheDto getCachedProductInfo(Long productId) {
        Optional<ProductEntity> entity = productRepository.findById(productId);
        String key = "product:" + productId;
        if (entity.isPresent()) {
            if (redisTemplate.opsForValue().get(key) == null) {
                redisTemplate.opsForValue().set(key, ProductCacheDto.from(entity.get()));
            }
        }
        return entity.map(ProductCacheDto::from).orElse(null);
    }

    @Transactional
    public Optional<ProductEntity> getProductInfoWithLock(Long productId) {
        return productRepository.getProductInfoWithLock(productId);
    }

    @Transactional(readOnly = true)
    public List<ProductCacheDto> getProductInfoList(Optional<BrandEntity> optionalBrandEntity, ProductSortOrder order, Integer size, Integer page) {
        if (page == null) {
            page = 0;
        } else if (page < 0) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "페이지는 최소 0 이상이여야 합니다.");
        }
        if (size == null) {
            size = 20;
        } else if (size < 1) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "페이지 크기는 최소 1 이상이여야 합니다.");
        }


        String key = "product-list:" + ":sort:" + (order == null ? "default" : order) + ":page:" + page;

        if (optionalBrandEntity.isEmpty() && page <= 3 && (size == 20 || size == 30 || size == 50)) {
            @SuppressWarnings("unchecked")
            ProductCacheDtoList cached = (ProductCacheDtoList) redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return cached.getList();
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> productEntityPage = productRepository.findProductsByBrandOrderBySortOrder(optionalBrandEntity, order, pageable);
        List<ProductCacheDto> cacheDtoList = productEntityPage.stream()
                .map(ProductCacheDto::from)
                .toList();
        if (optionalBrandEntity.isEmpty() && page <= 3 && (size == 20 || size == 30 || size == 50)) {
            redisTemplate.opsForValue().set(key, new ProductCacheDtoList(cacheDtoList), 5, TimeUnit.MINUTES);
        }
        return cacheDtoList;
    }

    @Transactional
    public ProductEntity getProductWithLockAndDecreaseQuantity(Long productId, Long quantity) {
        ProductEntity productEntity = productRepository.getProductInfoWithLock(productId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다."));
        if (!productEntity.getStatus().equals(ProductStatus.SALE)) {
            throw new CoreException(GlobalErrorType.CONFLICT, "상품이 판매중 상태가 아닙니다.");
        }
        productEntity.decreaseQuantity(quantity);
        if (productEntity.getStatus() == ProductStatus.SOLD_OUT) {
            redisTemplate.delete("product:" + productId);
        }
        return productEntity;
    }
}
