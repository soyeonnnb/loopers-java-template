package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

    @Schema(name = "사용자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserEntity user;

    @Schema(name = "총 금액")
    @Column(nullable = false)
    private Long totalPrice;

    @Schema(name = "주문 상품")
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    protected OrderEntity() {

    }

    public OrderEntity(UserEntity user, Long totalPrice) {
        if (user == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "주문 시, 사용자는 필수입니다.");
        }

        if (totalPrice == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문 가격은 필수입니다.");
        } else if (totalPrice < 0) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문 가격은 음수가 될 수 없습니다.");
        }

        this.user = user;
        this.totalPrice = totalPrice;
        this.items = new ArrayList<>();
    }

    public void addOrderItem(OrderItemEntity orderItem) {
        if (orderItem == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문 상품이 null이 될 수 없습니다.");
        }
        this.items.add(orderItem);
        orderItem.updateOrder(this);
    }
}
