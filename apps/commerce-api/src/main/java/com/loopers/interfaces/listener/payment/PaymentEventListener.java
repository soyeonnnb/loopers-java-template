package com.loopers.interfaces.listener.payment;

import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePayment(PaymentCreateEvent event) {
        log.info("payment create event 발생: {}, {}, {}", event.getUserId(), event.getPaymentId(), event.getOrderUuid());
        paymentService.payment(event.getUserId(), event.getPaymentId(), event.getOrderUuid(), event.getOrderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentFail(PaymentFailEvent event) {
        log.info("payment fail event 발생: {}", event.getPaymentId());
        paymentService.paymentFail(event.getPaymentId(), event.getReason());
    }


}
