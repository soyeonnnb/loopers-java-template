package com.loopers.interfaces.listener.like;

import com.loopers.application.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DisLikeEvent implements Event {
    private Long productId;
    private Long userId;
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
