package com.loopers.infrastructure.dataplatform;

import com.loopers.interfaces.listener.dataplatform.DataPlatformSendEvent;

public class OrderDataPlatformV1Dto {
    public record OrderSuccess(
            Long orderId,
            Long userId,
            Long price
    ) {

        public static OrderSuccess from(DataPlatformSendEvent event) {
            return new OrderSuccess(event.orderId(), event.userId(), event.totalPrice());
        }
    }
}
