package com.loopers.application.product;

import com.loopers.domain.product.ProductEntity;

public record ProductInfo(Long id, String name, Boolean isLike, BrandInfo brandInfo, Long price, Long quantity,
                          String description,
                          Long totalLikes) {
    public static ProductInfo from(ProductEntity productEntity, Boolean userLike, Long totalLikes) {
        // 일단 좋아요는 false 처리 -> 기능 구현 후 수정
        return new ProductInfo(productEntity.getId(), productEntity.getName(), userLike, BrandInfo.from(productEntity.getBrand()), productEntity.getPrice(), productEntity.getQuantity(), productEntity.getDescription(), totalLikes);
    }
}
