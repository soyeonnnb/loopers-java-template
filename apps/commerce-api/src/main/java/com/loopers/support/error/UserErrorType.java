package com.loopers.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorType implements BaseErrorType {
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 이메일 형식입니다."),
    INVALID_LOGIN_ID(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 사용자 ID 형식입니다."),
    INVALID_BIRTH_DATE(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 생년월일 형식입니다."),
    GENDER_CANNOT_BE_NULL(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "성별은 필수로 입력해야 합니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "중복된 사용자 ID 입니다."),
    USER_NOT_EXISTS(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), "해당 ID를 가지고 있는 사용자가 존재하지 않습니다.")
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
