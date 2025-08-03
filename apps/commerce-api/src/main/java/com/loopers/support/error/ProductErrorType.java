package com.loopers.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorType implements BaseErrorType {
    BRAND_ID_CANNOT_BE_NULL(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "BrandId는 null이 될 수 없습니다."),
    PRODUCT_ID_CANNOT_BE_NULL(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "ProductId는 null이 될 수 없습니다."),
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
