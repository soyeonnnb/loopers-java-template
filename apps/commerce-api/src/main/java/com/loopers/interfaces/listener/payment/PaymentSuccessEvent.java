package com.loopers.interfaces.listener.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentSuccessEvent {
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private String orderUuid;
    private Long totalPrice;
    private String paymentMethod;
}
