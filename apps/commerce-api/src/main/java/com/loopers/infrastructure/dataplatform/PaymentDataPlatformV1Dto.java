package com.loopers.infrastructure.dataplatform;

import com.loopers.interfaces.listener.dataplatform.DataPlatformSendEvent;

public class PaymentDataPlatformV1Dto {
    public record PaymentSuccess(
            Long paymentId,
            Long orderId,
            Long userId,
            String method,
            Long price
    ) {

        public static PaymentSuccess from(DataPlatformSendEvent event) {
            return new PaymentSuccess(event.paymentId(), event.orderId(), event.userId(), event.paymentMethod(), event.totalPrice());
        }
    }

    public record PaymentFailure(
            Long paymentId,
            Long orderId,
            Long userId,
            String method,
            Long price,
            String reason
    ) {
        public static PaymentFailure from(DataPlatformSendEvent event) {
            return new PaymentFailure(event.paymentId(), event.orderId(), event.userId(), event.paymentMethod(), event.totalPrice(), event.failReason());
        }
    }
}
