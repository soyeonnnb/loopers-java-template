package com.loopers.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorType implements BaseErrorType {
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 이메일 형식입니다."),
    INVALID_LOGIN_ID(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 사용자 ID 형식입니다."),
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
