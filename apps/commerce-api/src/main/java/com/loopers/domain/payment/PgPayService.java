package com.loopers.domain.payment;

import com.loopers.application.payment.PaymentGateway;
import com.loopers.infrastructure.payment.PgPaymentInfraV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PgPayService {

    private final PaymentGateway paymentGateway;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PgPaymentInfraV1Dto.PaymentResponse pay(Long userId, String orderUuid, String cardName, String cardNumber, Long totalPrice) {
        if (orderUuid == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드 결제 시, 주문 정보는 필수입니다.");
        }
        PgPaymentInfraV1Dto.PaymentResponse response = paymentGateway.requestPayment(userId, orderUuid, cardName, cardNumber, totalPrice);
        return response;
    }
}
