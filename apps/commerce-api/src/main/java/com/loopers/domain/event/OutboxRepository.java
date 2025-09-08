package com.loopers.domain.event;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OutboxRepository {
    OutboxEntity save(OutboxEntity outbox);

    List<OutboxEntity> findByStatusOrderByCreatedAt(OutboxEntity.OutboxStatus status, Pageable pageable);

    List<OutboxEntity> findFailedEventsForRetry(int maxRetryCount, Pageable pageable);
}
