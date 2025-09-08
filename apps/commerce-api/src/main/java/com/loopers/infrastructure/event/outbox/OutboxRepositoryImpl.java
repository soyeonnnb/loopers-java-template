package com.loopers.infrastructure.event.outbox;


import com.loopers.domain.event.OutboxEntity;
import com.loopers.domain.event.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository jpaRepository;

    @Override
    public OutboxEntity save(OutboxEntity outbox) {
        return jpaRepository.save(outbox);
    }

    @Override
    public List<OutboxEntity> findByStatusOrderByCreatedAt(OutboxEntity.OutboxStatus status, Pageable pageable) {
        return jpaRepository.findByStatusOrderByCreatedAt(status, pageable);
    }

    @Override
    public List<OutboxEntity> findFailedEventsForRetry(int maxRetryCount, Pageable pageable) {
        return jpaRepository.findByStatusAndRetryCountLessThan(
                OutboxEntity.OutboxStatus.FAILED, maxRetryCount, pageable);
    }
}
