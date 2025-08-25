package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.order.OrderEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity extends BaseEntity {

    @Schema(name = "주문")
    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Schema(name = "결제 방법")
    @Column(nullable = false)
    private PaymentMethod method;

    @Schema(name = "결제 카드")
    @ManyToOne(fetch = FetchType.LAZY)
    private CardEntity card;

    @Schema(name = "결제 상태")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionKey;
    private String failReason;

    public PaymentEntity(OrderEntity order, PaymentMethod method, CardEntity card, PaymentStatus status) {
        if (order == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 시 주문은 null이 될 수 없습니다.");
        }
        if (method == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 시 결제방식은 null이 될 수 없습니다.");
        }
        if (status == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 시 상태는 null이 될 수 없습니다.");
        }

        this.order = order;
        this.method = method;
        this.card = card;
        this.status = status;
    }


    public void updateOrder(OrderEntity order) {
        this.order = order;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void updateFailReason(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failReason = reason;
    }

    public void updateTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    public void paySuccess() {
        this.status = PaymentStatus.SUCCESS;
    }
}

