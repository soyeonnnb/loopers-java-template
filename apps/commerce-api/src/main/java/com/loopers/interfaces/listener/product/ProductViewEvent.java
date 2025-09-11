package com.loopers.interfaces.listener.product;

import com.loopers.application.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ProductViewEvent implements Event {

    private Long productId;
    private String userId;
    private LocalDateTime createdAt;

    @Override
    public LocalDateTime getOccurredAt() {
        return createdAt;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(productId);
    }
}
