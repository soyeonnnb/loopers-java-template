package com.loopers.interfaces.listener.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCouponUseEvent {
    private final Long userCouponId;
    private final Long useBeforePrice;
    private final Long paymentId;
    private final Long orderId;
}
