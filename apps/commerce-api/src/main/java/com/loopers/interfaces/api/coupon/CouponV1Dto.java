package com.loopers.interfaces.api.coupon;

import com.loopers.application.coupon.CouponInfo;

import java.time.ZonedDateTime;

public class CouponV1Dto {
    public record CouponResponse(Long id, String name, String type, Long minOrderPrice, Long maxUsePrice, Double rate,
                                 ZonedDateTime expiryDate) {
        public static CouponResponse from(CouponInfo coupon) {
            return new CouponResponse(coupon.id(), coupon.name(), coupon.type(), coupon.minOrderPrice(), coupon.maxUsePrice(), coupon.rate(), coupon.expiryDate());
        }
    }
}
