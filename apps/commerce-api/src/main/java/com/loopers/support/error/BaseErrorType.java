package com.loopers.support.error;

import org.springframework.http.HttpStatus;

public interface BaseErrorType {

    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
