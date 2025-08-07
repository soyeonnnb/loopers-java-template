package com.loopers.application.coupon;

import com.loopers.domain.coupon.UserCouponEntity;

import java.time.ZonedDateTime;

public record UserCouponInfo(Long id, CouponInfo coupon, ZonedDateTime expiredAt, ZonedDateTime usedAt, Long beforePrice) {

    public static UserCouponInfo from(UserCouponEntity userCoupon) {
        if (userCoupon == null) {
            return null;
        }
        return new UserCouponInfo(userCoupon.getId(), CouponInfo.from(userCoupon.getCoupon()), userCoupon.getExpiredAt(), userCoupon.getUsedAt(), userCoupon.getBeforePrice());
    }
}

