package com.loopers.interfaces.listener.payment;

import com.loopers.application.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PaymentFailEvent implements Event {
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private String reason;
    private LocalDateTime failedAt;

    @Override
    public LocalDateTime getOccurredAt() {
        return failedAt;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(paymentId);
    }
}
