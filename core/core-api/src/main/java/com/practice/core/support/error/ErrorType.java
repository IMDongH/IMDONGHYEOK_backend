package com.practice.core.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            LogLevel.ERROR),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E400, "요청이 올바르지 않습니다.", LogLevel.INFO),
    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST, ErrorCode.E401, "해당 데이터를 찾을 수 없습니다.", LogLevel.ERROR),

    // 계좌
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E1001, "계좌를 찾을 수 없습니다.", LogLevel.INFO),
    DUPLICATE_ACCOUNT_NUMBER(HttpStatus.BAD_REQUEST, ErrorCode.E1002, "이미 존재하는 계좌번호입니다.", LogLevel.INFO),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, ErrorCode.E1003, "잔액이 부족합니다.", LogLevel.INFO),
    EXCEED_DAILY_WITHDRAW_LIMIT(HttpStatus.CONFLICT, ErrorCode.E1004, "일일 출금 한도를 초과했습니다.", LogLevel.INFO);

    private final HttpStatus status;
    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }
}
