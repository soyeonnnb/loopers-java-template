package com.loopers.interfaces.listener.payment;

import com.loopers.application.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PaymentSuccessEvent implements Event {
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private String orderUuid;
    private Long totalPrice;
    private String paymentMethod;
    private LocalDateTime createdAt;

    @Override
    public LocalDateTime getOccurredAt() {
        return createdAt;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(paymentId);
    }
}
