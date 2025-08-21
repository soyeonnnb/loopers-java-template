package com.loopers.domain.payment;

import com.loopers.application.payment.PaymentGateway;
import com.loopers.domain.order.OrderEntity;
import com.loopers.infrastructure.payment.PgPaymentInfraV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PgPayService {

    private final PaymentGateway paymentGateway;

    @Transactional
    public Boolean pay(OrderEntity order) {
        if (order == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드 결제 시, 주문 정보는 필수입니다.");
        }
        if (!order.getPayment().getMethod().equals(PaymentMethod.CARD)) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드 결제를 하려면 주문 결제 방식이 카드여야 합니다.");
        }

        PgPaymentInfraV1Dto.PaymentResponse response = paymentGateway.requestPayment(order.getUser().getId(), order.getUuid(), order.getPayment().getCard().getType().name(), order.getPayment().getCard().getNumber(), order.getTotalPrice());
        if (response.isSuccess()) {
            order.updateTransactionKey(response.transactionKey());
        } else {
            order.payFailed(response.reason());
        }
        return response.isSuccess();
    }
}
