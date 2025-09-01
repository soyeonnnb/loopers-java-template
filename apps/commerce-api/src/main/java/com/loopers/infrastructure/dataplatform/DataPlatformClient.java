package com.loopers.infrastructure.dataplatform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataPlatformClient {
    public void sendOrderData(OrderDataPlatformV1Dto.OrderSuccess orderInfo) {
        log.info("데이터 플랫폼으로 주문 데이터 전송: {}", orderInfo);

        // 시뮬레이션을 위한 지연
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendPaymentData(PaymentDataPlatformV1Dto.PaymentSuccess paymentInfo) {
        log.info("데이터 플랫폼으로 결제 데이터 전송: {}", paymentInfo);
    }

    public void sendPaymentFailureData(PaymentDataPlatformV1Dto.PaymentFailure failureData) {
        log.info("데이터 플랫폼으로 결제 실패 데이터 전송: {}", failureData);
    }

}
