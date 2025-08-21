package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "orders_item")
public class OrderItemEntity extends BaseEntity {
    @Schema(name = "주문")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private OrderEntity order;

    @Schema(name = "상품")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductEntity product;

    @Schema(name = "상품 가격")
    @Column(nullable = false)
    private Long price;

    @Schema(name = "개수")
    @Column(nullable = false)
    private Long quantity;

    protected OrderItemEntity() {

    }

    public OrderItemEntity(OrderEntity order, ProductEntity product, Long quantity) {
        if (order == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문은 필수입니다.");
        }
        if (product == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품은 필수입니다.");
        }
        if (quantity == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "수량은 필수입니다.");
        } else if (quantity <= 0) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "수량은 1 이상이여야 합니다.");
        }
        this.order = order;
        this.product = product;
        this.price = product.getPrice();
        this.quantity = quantity;
    }

    public void updateOrder(OrderEntity order) {
        this.order = order;
    }
}
