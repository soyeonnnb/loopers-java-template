package com.loopers.application.event;

import com.loopers.infrastructure.dataplatform.DataPlatformClient;
import com.loopers.interfaces.listener.order.OrderCompletedEvent;
import com.loopers.interfaces.listener.payment.PaymentFailEvent;
import com.loopers.interfaces.listener.payment.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataPlatformEventHandler {

    private final DataPlatformClient dataPlatformClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("주문 성공 dataPlatform 호출");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("결제 성공 dataPlatform 호출");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handlePaymentFailed(PaymentFailEvent event) {
        log.info("결제 실패 dataPlatform 호출");
    }
}
