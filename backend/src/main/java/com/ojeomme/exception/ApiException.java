package com.ojeomme.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ApiErrorCode errorCode;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.errorCode = errorCode;
    }

    public ApiException(ApiErrorCode errorCode, String cause) {
        super(errorCode.getMessage(), new Throwable(cause));
        this.httpStatus = errorCode.getHttpStatus();
        this.errorCode = errorCode;
    }
}
