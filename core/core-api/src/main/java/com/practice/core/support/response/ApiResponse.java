package com.practice.core.support.response;

import com.practice.core.support.error.ErrorMessage;
import com.practice.core.support.error.ErrorType;

public class ApiResponse<T> {
    private final ResultType result;
    private final T data;
    private final ErrorMessage error;

    private ApiResponse(ResultType result, T data, ErrorMessage error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public static ApiResponse<Object> success() {
        return new ApiResponse<>(ResultType.SUCCESS, null, null);
    }

    public static <S> ApiResponse<S> success(S data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static <S> ApiResponse<S> error(ErrorType error, Object errorData) {
        return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(error, errorData));
    }

    public static <S> ApiResponse<S> error(ErrorType error) {
        return error(error, null);
    }

    public ResultType getResult() {
        return result;
    }

    public T getData() {
        return data;
    }

    public ErrorMessage getError() {
        return error;
    }
}
