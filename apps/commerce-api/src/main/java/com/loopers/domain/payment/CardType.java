package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;

public enum CardType {
    SAMSUNG, KB, HYUNDAI;

    public static CardType from(String type) {
        try {
            return CardType.valueOf(type);
        } catch (NullPointerException e) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드 타입은 null이 될 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드 타입 형식이 아닙니다.");
        }
    }
}
