package com.loopers.application.payment;

import com.loopers.infrastructure.payment.PgPaymentInfraV1Dto;

public interface PaymentGateway {
    PgPaymentInfraV1Dto.PaymentResponse requestPayment(Long userId, String orderUUID, String cardType, String cardNo, Long amount);

    PgPaymentInfraV1Dto.PgPaymentInfoResponse getPaymentInfo(Long userId, String transactionKey);
}
