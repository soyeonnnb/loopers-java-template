package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.coupon.UserCouponEntity;
import com.loopers.domain.payment.PaymentEntity;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

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

    @Schema(name = "사용 쿠폰")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private UserCouponEntity userCoupon;

    @Enumerated(EnumType.STRING)
    @Schema(name = "주문 상태")
    @Column(nullable = false)
    private OrderStatus status;

    @Schema(name = "결제")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentEntity payment;

    @Schema(name = "주문 UUID")
    @UuidGenerator
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(36)")
    private String uuid;

    protected OrderEntity() {

    }

    public OrderEntity(UserEntity user, Long totalPrice, UserCouponEntity userCoupon) {
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
        this.userCoupon = userCoupon;
        this.status = OrderStatus.PENDING;
    }

    public void addPayment(PaymentEntity paymentEntity) {
        if (paymentEntity == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 방식이 null이 될 수 없습니다.");
        }
        this.payment = paymentEntity;
        paymentEntity.updateOrder(this);
    }

    public void addOrderItem(OrderItemEntity orderItem) {
        if (orderItem == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문 상품이 null이 될 수 없습니다.");
        }
        this.items.add(orderItem);
        orderItem.updateOrder(this);
    }

    public void updateTransactionKey(String transactionKey) {
        this.payment.updateTransactionKey(transactionKey);
    }


    public void payFailed(String reason) {
        this.status = OrderStatus.FAILED;
        this.payment.updateFailReason(reason);
    }

    public void paySuccess() {
        this.status = OrderStatus.COMPLETE;
        this.payment.paySuccess();
    }
}
