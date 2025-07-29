package com.loopers.domain.product;


import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product")
public class ProductEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "브랜드", nullable = false)
    private BrandEntity brand;

    @Column(name = "상품명", nullable = false)
    private String name;

    @Column(name = "상품가격", nullable = false)
    private Long price;

    @Column(name = "재고", nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "상품 상태", nullable = false)
    private ProductStatus status;

    @Column(name = "상품 설명", nullable = false)
    private String description;

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
}
