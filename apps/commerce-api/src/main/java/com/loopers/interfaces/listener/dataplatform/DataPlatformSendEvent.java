package com.loopers.interfaces.listener.dataplatform;

import java.time.LocalDateTime;

public record DataPlatformSendEvent(
        String eventType,      // "PAYMENT_SUCCESS", "PAYMENT_FAIL", "ORDER_COMPLETE"
        Long orderId,
        Long paymentId,
        Long userId,
        String orderUuid,
        Long totalPrice,
        String paymentMethod,
        String failReason,     // 실패시에만
        LocalDateTime dateTime
) {
    public static DataPlatformSendEvent paymentSuccess(Long paymentId, Long orderId, Long userId, String orderUuid, Long totalPrice, String paymentMethod) {
        return new DataPlatformSendEvent("PAYMENT_SUCCESS", orderId, paymentId, userId, orderUuid, totalPrice, paymentMethod, null, LocalDateTime.now());
    }

    public static DataPlatformSendEvent paymentFail(Long paymentId, Long orderId, String failReason) {
        return new DataPlatformSendEvent("PAYMENT_FAIL", orderId, paymentId, null, null, null, null, failReason, LocalDateTime.now());
    }

    public static DataPlatformSendEvent orderComplete(Long orderId, Long userId, String orderUuid, Long totalPrice) {
        return new DataPlatformSendEvent("ORDER_COMPLETE", orderId, null, userId, orderUuid, totalPrice, null, null, LocalDateTime.now());
    }
}
