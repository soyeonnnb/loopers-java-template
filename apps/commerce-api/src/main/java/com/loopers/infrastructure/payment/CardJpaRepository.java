package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CardJpaRepository extends JpaRepository<CardEntity, Long> {
    CardEntity save(CardEntity cardEntity);
}
