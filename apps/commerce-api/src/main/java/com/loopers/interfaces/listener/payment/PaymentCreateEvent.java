package com.loopers.interfaces.listener.payment;

import com.loopers.domain.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentCreateEvent {
    private Long userId;
    private Long paymentId;
    private String orderUuid;
    private PaymentMethod method;
    private Long orderId;
}
