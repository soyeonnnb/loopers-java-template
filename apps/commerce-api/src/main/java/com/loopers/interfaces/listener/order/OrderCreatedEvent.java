package com.loopers.interfaces.listener.order;

import com.loopers.application.event.Event;
import com.loopers.kafka.message.payload.OrderEventPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class OrderCreatedEvent implements Event {

    private Long orderId;
    private String userId;
    private String orderUuid;
    private Long totalAmount;
    private List<OrderEventPayload.OrderItem> orderItems;
    private LocalDateTime createdAt;

    @Override
    public LocalDateTime getOccurredAt() {
        return createdAt;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(orderId);
    }


}
