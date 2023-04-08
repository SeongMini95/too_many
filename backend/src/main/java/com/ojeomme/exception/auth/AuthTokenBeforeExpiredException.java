package com.ojeomme.exception.auth;

import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;

public class AuthTokenBeforeExpiredException extends ApiException {

    public AuthTokenBeforeExpiredException() {
        super(ApiErrorCode.O_AUTH_TOKEN_BEFORE_EXPIRED);
    }
}
