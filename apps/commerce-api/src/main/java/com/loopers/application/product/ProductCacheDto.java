package com.loopers.application.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductStatus;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class ProductCacheDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final Long brandId;
    private final String brandName;
    private final String name;
    private final Long price;
    private final Long quantity;
    private final ProductStatus status;
    private final String description;
    private final LocalDateTime saleStartAt;
    private final Long likeCount;

    @JsonCreator
    public ProductCacheDto(
            @JsonProperty("id") Long id,
            @JsonProperty("brandId") Long brandId,
            @JsonProperty("brandName") String brandName,
            @JsonProperty("name") String name,
            @JsonProperty("price") Long price,
            @JsonProperty("quantity") Long quantity,
            @JsonProperty("status") ProductStatus status,
            @JsonProperty("description") String description,
            @JsonProperty("saleStartAt") LocalDateTime saleStartAt,
            @JsonProperty("likeCount") Long likeCount) {
        this.id = id;
        this.brandId = brandId;
        this.brandName = brandName;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.description = description;
        this.saleStartAt = saleStartAt;
        this.likeCount = likeCount;
    }

    public static ProductCacheDto from(ProductEntity entity) {
        return new ProductCacheDto(
                entity.getId(),
                entity.getBrand().getId(),
                entity.getBrand().getName(),
                entity.getName(),
                entity.getPrice(),
                entity.getQuantity(),
                entity.getStatus(),
                entity.getDescription(),
                entity.getSaleStartAt(),
                entity.getProductCount().getLikeCount()
        );
    }
}
