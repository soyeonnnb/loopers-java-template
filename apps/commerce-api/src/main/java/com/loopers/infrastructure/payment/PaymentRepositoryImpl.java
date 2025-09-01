package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PaymentEntity;
import com.loopers.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public PaymentEntity save(PaymentEntity paymentEntity) {
        return paymentJpaRepository.save(paymentEntity);
    }

    @Override
    public Optional<PaymentEntity> findById(Long paymentId) {
        return paymentJpaRepository.findById(paymentId);
    }
}
