package com.loopers.domain.payment;

import java.util.Optional;

public interface PaymentRepository {
    PaymentEntity save(PaymentEntity paymentEntity);

    Optional<PaymentEntity> findById(Long paymentId);
}
