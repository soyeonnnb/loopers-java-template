package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;

public enum CouponType {
    FLAT, RATE;

    public static CouponType from(String type) {
        try {
            return CouponType.valueOf(type);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰 타입이 유효하지 않습니다.");
        }
    }
}
