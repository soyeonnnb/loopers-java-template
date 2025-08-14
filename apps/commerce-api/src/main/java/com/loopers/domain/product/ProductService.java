package com.loopers.domain.product;

import com.loopers.application.product.ProductCacheDto;
import com.loopers.application.product.ProductSortOrder;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Optional<ProductEntity> getProductInfo(Long productId) {
        return productRepository.findById(productId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "'product:' + #productId")
    public ProductCacheDto getCachedProductInfo(Long productId) {
        Optional<ProductEntity> entity = productRepository.findById(productId);
        return entity.map(ProductCacheDto::from).orElse(null);
    }

    @Transactional
    public Optional<ProductEntity> getProductInfoWithLock(Long productId) {
        return productRepository.getProductInfoWithLock(productId);
    }

    @Transactional(readOnly = true)
    public Page<ProductEntity> getProductInfoList(Optional<BrandEntity> optionalBrandEntity, ProductSortOrder order, Integer size, Integer page) {
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

        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findProductsByBrandOrderBySortOrder(optionalBrandEntity, order, pageable);
    }
}
