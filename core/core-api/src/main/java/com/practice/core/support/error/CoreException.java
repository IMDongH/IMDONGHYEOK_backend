package com.practice.core.support.error;

public class CoreException extends RuntimeException {
    private final ErrorType errorType;
    private final Object data;

    public CoreException(ErrorType errorType) {
        this(errorType, null);
    }

    public CoreException(ErrorType errorType, Object data) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = data;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Object getData() {
        return data;
    }
}
