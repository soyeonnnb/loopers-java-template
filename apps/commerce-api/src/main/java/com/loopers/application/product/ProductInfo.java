package com.loopers.application.product;

import com.loopers.domain.product.ProductEntity;

public record ProductInfo(Long id, String name, Boolean isLike, BrandInfo brandInfo, Long price, Long quantity,
                          String description,
                          Long totalLikes) {
    public static ProductInfo from(ProductEntity productEntity, Boolean userLike) {
        // 일단 좋아요는 false 처리 -> 기능 구현 후 수정
        return new ProductInfo(productEntity.getId(), productEntity.getName(), userLike, BrandInfo.from(productEntity.getBrand()), productEntity.getPrice(), productEntity.getQuantity(), productEntity.getDescription(), productEntity.getProductCount().getLikeCount());
    }

    public static ProductInfo from(ProductEntity productEntity) {
        return new ProductInfo(productEntity.getId(), productEntity.getName(), false, BrandInfo.from(productEntity.getBrand()), productEntity.getPrice(), productEntity.getQuantity(), productEntity.getDescription(), productEntity.getProductCount().getLikeCount());
    }

    public static ProductInfo from(ProductCacheDto cacheDto, Boolean userLike) {
        return new ProductInfo(
                cacheDto.getId(),
                cacheDto.getName(),
                userLike,
                new BrandInfo(cacheDto.getBrandId(), cacheDto.getBrandName()),
                cacheDto.getPrice(),
                cacheDto.getQuantity(),
                cacheDto.getDescription(),
                cacheDto.getLikeCount()
        );
    }

    public static ProductInfo from(ProductCacheDto cacheDto) {
        return from(cacheDto, false);
    }
}
