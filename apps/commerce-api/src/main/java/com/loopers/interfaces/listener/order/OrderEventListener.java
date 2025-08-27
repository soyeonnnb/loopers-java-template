package com.loopers.interfaces.listener.order;

import com.loopers.interfaces.listener.dataplatform.DataPlatformSendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("주문 완료 이벤트 처리: orderId={}", event.orderId());

        // 데이터 플랫폼 전송 이벤트 발행
        eventPublisher.publishEvent(DataPlatformSendEvent.orderComplete(
                event.orderId(),
                event.userId(),
                event.orderUuid(),
                event.totalPrice()
        ));

        // 사용자 행동 로깅 이벤트도 발행 가능
//        eventPublisher.publishEvent(new UserActionEvent("ORDER_COMPLETE", event.userId(), event.orderId()));
    }
}
