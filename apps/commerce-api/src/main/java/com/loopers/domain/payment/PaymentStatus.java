package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;

public enum PaymentStatus {
    PENDING, SUCCESS, FAILED;

    public static PaymentStatus from(String status) {
        try {
            return PaymentStatus.valueOf(status);
        } catch (NullPointerException e) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 상태는 null이 될 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 상태 형식이 아닙니다.");
        }
    }
}
