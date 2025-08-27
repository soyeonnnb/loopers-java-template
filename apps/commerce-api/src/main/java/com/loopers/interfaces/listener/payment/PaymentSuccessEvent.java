package com.loopers.interfaces.listener.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentSuccessEvent {
    private Long paymentId;
}
