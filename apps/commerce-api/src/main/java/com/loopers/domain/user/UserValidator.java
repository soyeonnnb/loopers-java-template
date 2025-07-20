package com.loopers.domain.user;


import com.loopers.support.error.CoreException;
import com.loopers.support.error.UserErrorType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {
    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern BIRTH_PATTERN = Pattern.compile("^(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

    public static void validateLoginId(String loginId) {
        if (loginId == null || loginId.isBlank() || !LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            throw new CoreException(UserErrorType.INVALID_LOGIN_ID);
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new CoreException(UserErrorType.INVALID_EMAIL);
        }
    }

    public static void validateBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isBlank() || !BIRTH_PATTERN.matcher(birthDate).matches()) {
            throw new CoreException(UserErrorType.INVALID_BIRTH_DATE);
        }

    }

}


