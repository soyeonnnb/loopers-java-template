package com.loopers.interfaces.listener.dataplatform;

import com.loopers.infrastructure.dataplatform.DataPlatformClient;
import com.loopers.infrastructure.dataplatform.OrderDataPlatformV1Dto;
import com.loopers.infrastructure.dataplatform.PaymentDataPlatformV1Dto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPlatformEventListener {

    private final DataPlatformClient dataPlatformClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleDataPlatformSend(DataPlatformSendEvent event) {
        try {
            switch (event.eventType()) {
                case "PAYMENT_SUCCESS" -> {
                    PaymentDataPlatformV1Dto.PaymentSuccess data = PaymentDataPlatformV1Dto.PaymentSuccess.from(event);
                    dataPlatformClient.sendPaymentData(data);
                }
                case "PAYMENT_FAIL" -> {
                    PaymentDataPlatformV1Dto.PaymentFailure data = PaymentDataPlatformV1Dto.PaymentFailure.from(event);
                    dataPlatformClient.sendPaymentFailureData(data);
                }
                case "ORDER_COMPLETE" -> {
                    OrderDataPlatformV1Dto.OrderSuccess data = OrderDataPlatformV1Dto.OrderSuccess.from(event);
                    dataPlatformClient.sendOrderData(data);
                }
            }
            log.info("데이터 플랫폼 전송 완료: {}", event.eventType());
        } catch (Exception e) {
            log.error("데이터 플랫폼 전송 실패: {}, error={}", event.eventType(), e.getMessage());
        }
    }

}
