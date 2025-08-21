package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;

public enum PaymentMethod {
    POINT, CARD;

    public static PaymentMethod from(String method) {
        try {
            return PaymentMethod.valueOf(method);
        } catch (NullPointerException e) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 타입은 null이 될 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 타입 형식이 아닙니다.");
        }
    }
}
