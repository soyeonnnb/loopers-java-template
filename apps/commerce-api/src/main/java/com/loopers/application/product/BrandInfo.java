package com.loopers.application.product;

import com.loopers.domain.product.BrandEntity;

public record BrandInfo(Long id, String name) {
    public static BrandInfo from(BrandEntity brandEntity) {
        return new BrandInfo(brandEntity.getId(), brandEntity.getName());
    }
}
