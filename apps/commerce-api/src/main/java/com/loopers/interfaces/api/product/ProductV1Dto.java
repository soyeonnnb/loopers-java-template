package com.loopers.interfaces.api.product;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.loopers.application.product.BrandInfo;
import com.loopers.application.product.ProductInfo;

public class ProductV1Dto {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record ProductResponse(Long id, String name, Boolean isLike, BrandInfo brandInfo, Long price, String description,
                                  Long totalLikes, Long rank, Double score) {
        public ProductResponse(Long id, String name, Boolean isLike, BrandInfo brandInfo, Long price, String description,
                               Long totalLikes, Long rank, Double score) {
            this.id = id;
            this.name = name;
            this.isLike = isLike;
            this.brandInfo = brandInfo;
            this.price = price;
            this.description = description;
            this.totalLikes = totalLikes;
            this.rank = rank;
            this.score = score;
        }

        public static ProductResponse from(ProductInfo productInfo) {
            return new ProductResponse(productInfo.id(), productInfo.name(), productInfo.isLike(), productInfo.brandInfo(), productInfo.price(), productInfo.description()
                    , productInfo.totalLikes(), productInfo.rank(), productInfo.score());
        }
    }
}
