package com.ojeomme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<String> handlerUserApiException(ApiException e) {
        Throwable throwable = e.getCause();
        if (throwable != null) {
            log.error(throwable.getMessage(), e);
        }

        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(MissingRequestValueException.class)
    protected ResponseEntity<String> handlerMissingRequestValue(MissingRequestValueException e) {
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<String> handlerHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<String> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = "";
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> handlerException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body("예상치 못한 오류가 발생했습니다.\n관리자에게 문의 바랍니다.");
    }
}
