package com.loopers.interfaces.listener.order;

import java.time.LocalDateTime;

public record OrderCompletedEvent(
        Long orderId,
        Long userId,
        String orderUuid,
        Long totalPrice,
        LocalDateTime completedAt
) {
}
