package com.loopers.support.error;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
    private final BaseErrorType errorType;
    private final String customMessage;

    public CoreException(BaseErrorType errorType) {
        this(errorType, null);
    }

    public CoreException(BaseErrorType errorType, String customMessage) {
        super(customMessage != null ? customMessage : errorType.getMessage());
        this.errorType = errorType;
        this.customMessage = customMessage;
    }
}
