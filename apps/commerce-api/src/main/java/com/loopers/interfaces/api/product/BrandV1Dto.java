package com.loopers.interfaces.api.product;


import com.loopers.application.product.BrandInfo;

public class BrandV1Dto {
    public record BrandResponse(Long id, String name) {
        public BrandResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public static BrandResponse from(BrandInfo brandInfo) {
            return new BrandResponse(brandInfo.id(), brandInfo.name());
        }
    }
}
