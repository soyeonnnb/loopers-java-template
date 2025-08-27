package com.loopers.interfaces.listener.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentFailEvent {
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private String reason;
}
