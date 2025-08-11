package com.loopers.application.coupon;

import com.loopers.domain.coupon.CouponEntity;

import java.time.ZonedDateTime;

public record CouponInfo(Long id, String name, String type, Long minOrderPrice, Long maxUsePrice, Double rate,
                         ZonedDateTime expiryDate) {

    public static CouponInfo from(CouponEntity coupon) {
        return new CouponInfo(coupon.getId(), coupon.getName(), coupon.getType().name(), coupon.getMinOrderPrice(), coupon.getMaxUsePrice(), coupon.getRate(), coupon.getExpiryDate());
    }
}
