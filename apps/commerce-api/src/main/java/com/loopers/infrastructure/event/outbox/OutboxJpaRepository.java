package com.loopers.infrastructure.event.outbox;


import com.loopers.domain.event.OutboxEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, Long> {

    List<OutboxEntity> findByStatusOrderByCreatedAt(OutboxEntity.OutboxStatus status, Pageable pageable);

    List<OutboxEntity> findByStatusAndRetryCountLessThan(
            OutboxEntity.OutboxStatus status, Integer retryCount, Pageable pageable);
}
