package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public class CardValidator {
    private static final Pattern CARD_PATTERN = Pattern.compile("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$");

    public static void validateCardNumber(String number) {
        if (number == null || number.isBlank()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드 번호는 필수입니다.");
        }
        if (!CARD_PATTERN.matcher(number).matches()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드 형식이 아닙니다.");
        }
    }
}

