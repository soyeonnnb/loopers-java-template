package com.loopers.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointErrorType implements BaseErrorType {
    POINT_MUST_BE_GREATER_THAN_0(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "충전할 포인트는 0 이하면 안됩니다."),
     ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
