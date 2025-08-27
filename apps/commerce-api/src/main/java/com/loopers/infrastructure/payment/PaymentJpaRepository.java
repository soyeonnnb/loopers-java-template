package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PaymentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
    PaymentEntity save(PaymentEntity paymentEntity);

    @EntityGraph(attributePaths = {"order", "card"})
    Optional<PaymentEntity> findById(Long paymentId);
}
