package com.loopers.interfaces.api.product;


import com.loopers.application.product.BrandInfo;
import com.loopers.application.product.ProductInfo;

public class ProductV1Dto {
    public record ProductResponse(Long id, String name, Boolean isLike, BrandInfo brandInfo, Long price, String description,
                                  Long totalLikes) {
        public ProductResponse(Long id, String name, Boolean isLike, BrandInfo brandInfo, Long price, String description,
                               Long totalLikes) {
            this.id = id;
            this.name = name;
            this.isLike = isLike;
            this.brandInfo = brandInfo;
            this.price = price;
            this.description = description;
            this.totalLikes = totalLikes;
        }

        public static ProductResponse from(ProductInfo productInfo) {
            return new ProductResponse(productInfo.id(), productInfo.name(), productInfo.isLike(), productInfo.brandInfo(), productInfo.price(), productInfo.description()
                    , productInfo.totalLikes());
        }
    }
}
