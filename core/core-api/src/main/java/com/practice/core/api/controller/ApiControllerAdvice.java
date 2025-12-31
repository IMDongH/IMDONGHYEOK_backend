package com.practice.core.api.controller;

import com.practice.core.support.error.CoreException;
import com.practice.core.support.error.ErrorType;
import com.practice.core.support.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResponse<Object>> handleCoreException(CoreException e) {
        LogLevel logLevel = e.getErrorType().getLogLevel();
        if (logLevel == LogLevel.ERROR) {
            log.error("CoreException : {}", e.getMessage(), e);
        } else if (logLevel == LogLevel.WARN) {
            log.warn("CoreException : {}", e.getMessage(), e);
        } else {
            log.info("CoreException : {}", e.getMessage(), e);
        }
        return new ResponseEntity<>(ApiResponse.error(e.getErrorType(), e.getData()), e.getErrorType().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResponse.error(ErrorType.DEFAULT_ERROR), ErrorType.DEFAULT_ERROR.getStatus());
    }
}
