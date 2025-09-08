package com.loopers.interfaces.listener.payment;

import com.loopers.application.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PaymentCreateEvent implements Event {
    private Long userId;
    private Long paymentId;
    private String orderUuid;
    private String method;
    private Long orderId;
    private LocalDateTime occuredAt;

    @Override
    public LocalDateTime getOccurredAt() {
        return occuredAt;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(paymentId);
    }
}
