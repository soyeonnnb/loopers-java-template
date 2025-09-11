package com.loopers.application.product;

import com.loopers.application.event.EventPublisher;
import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.BrandService;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingService;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.listener.product.ProductViewEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final UserService userService;
    private final ProductService productService;
    private final LikeService likeService;
    private final BrandService brandService;
    private final RankingService rankingService;
    private final EventPublisher eventPublisher;

    @Transactional
    public ProductInfo getProductInfo(String userId, Long productId) {
        if (productId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품 ID가 존재하지 않습니다.");
        }

        ProductCacheDto productCacheDto = productService.getCachedProductInfo(productId);
        if (productCacheDto == null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 ID에 해당하는 데이터가 없습니다.");
        }

        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);

        boolean isLike = false;
        if (optionalUserEntity.isPresent()) {
            Optional<LikeEntity> optionalLikeEntity = likeService.getUserLikeProduct(optionalUserEntity.get().getId(), productId);
            isLike = optionalLikeEntity.isPresent() && optionalLikeEntity.get().getIsLike();
        }

        Long rank = rankingService.getRank(LocalDate.now(), productId);
        Double score = rankingService.getScore(LocalDate.now(), productId);

        eventPublisher.publish(new ProductViewEvent(productId, userId, LocalDateTime.now()));
        return ProductInfo.from(productCacheDto, isLike, rank, score);
    }

    public List<ProductInfo> getProductInfoList(String userId, Long brandId, ProductSortOrder order, Integer size, Integer page) {
        Optional<UserEntity> optionalUserEntity = Optional.empty();
        if (userId != null) {
            optionalUserEntity = userService.getUserInfo(userId);
            if (optionalUserEntity.isEmpty()) {
                throw new CoreException(GlobalErrorType.NOT_FOUND, "사용자 정보를 찾을 수 없습니다.");
            }
        }
        Optional<BrandEntity> optionalBrandEntity = Optional.empty();
        if (brandId != null) {
            optionalBrandEntity = brandService.getBrandInfo(brandId);
            if (optionalBrandEntity.isEmpty()) {
                throw new CoreException(GlobalErrorType.NOT_FOUND, "브랜드 정보를 찾을 수 없습니다.");
            }
        }

        List<ProductCacheDto> productCacheDtoList = productService.getProductInfoList(optionalBrandEntity, order, size, page);
        List<ProductInfo> productInfoList = new ArrayList<>();
        for (ProductCacheDto productCacheDto : productCacheDtoList) {
            Optional<LikeEntity> optionalLikeEntity = Optional.empty();
            if (optionalUserEntity.isPresent()) {
                optionalLikeEntity = likeService.getUserLikeProduct(optionalUserEntity.get().getId(), productCacheDto.getId());
            }
            Long rank = rankingService.getRank(LocalDate.now(), productCacheDto.getId());
            Double score = rankingService.getScore(LocalDate.now(), productCacheDto.getId());
            productInfoList.add(ProductInfo.from(productCacheDto, optionalLikeEntity.isPresent() && optionalLikeEntity.get().getIsLike(), rank, score));
        }
        return productInfoList;
    }

}
