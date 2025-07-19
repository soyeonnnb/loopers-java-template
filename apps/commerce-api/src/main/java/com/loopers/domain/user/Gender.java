package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.UserErrorType;

public enum Gender {
    FEMALE,
    MALE,
    UNKNOWN;

    public static Gender from(String gender) {
        try {
            return Gender.valueOf(gender);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CoreException(UserErrorType.INVALID_GENDER);
        }
    }
}
