package com.loopers.interfaces.listener.coupon;

import com.loopers.application.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserCouponUseEvent implements Event {
    private final Long userCouponId;
    private final Long useBeforePrice;
    private final Long paymentId;
    private final Long orderId;
    private final LocalDateTime usedAt;

    @Override
    public LocalDateTime getOccurredAt() {
        return usedAt;
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(userCouponId);
    }
}
