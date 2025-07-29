package com.loopers.domain.product;


import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Getter
@Table(name = "product")
public class ProductEntity extends BaseEntity {

    @Schema(name = "브랜드")
    @ManyToOne
    @JoinColumn(nullable = false)
    private BrandEntity brand;

    @Schema(name = "상품명")
    @Column(nullable = false)
    private String name;

    @Schema(name = "상품가격")
    @Column(nullable = false)
    private Long price;

    @Schema(name = "재고")
    @Column(nullable = false)
    private Long quantity;

    @Schema(name = "상품 상태")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Schema(name = "상품 설명")
    @Column(nullable = false)
    private String description;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private ProductCountEntity productCount;

    protected ProductEntity() {
    }

    public ProductEntity(BrandEntity brandEntity, String name, Long price, Long quantity, ProductStatus status, String description) {
        if (brandEntity == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "브랜드는 NULL 일 수 없습니다.");
        }

        if (name == null || name.isBlank()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "이름은 필수로 입력하셔야 합니다.");
        }

        if (price == null || price < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "금액은 음수일 수 없습니다.");
        }

        if (quantity == null || quantity < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "재고는 음수일 수 없습니다.");
        }
        this.brand = brandEntity;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
