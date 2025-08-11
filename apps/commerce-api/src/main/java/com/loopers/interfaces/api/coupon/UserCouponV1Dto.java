package com.loopers.interfaces.api.coupon;

import com.loopers.application.coupon.UserCouponInfo;

import java.time.ZonedDateTime;

public class UserCouponV1Dto {
    public record UserCouponResponse(Long id, CouponV1Dto.CouponResponse coupon, ZonedDateTime expiredAt, ZonedDateTime usedAt,
                                     Long beforePrice) {

        public static UserCouponResponse from(UserCouponInfo userCoupon) {
            if (userCoupon == null) {
                return null;
            }
            return new UserCouponResponse(userCoupon.id(), CouponV1Dto.CouponResponse.from(userCoupon.coupon()), userCoupon.expiredAt(), userCoupon.usedAt(), userCoupon.beforePrice());
        }
    }
}
