package com.toomany.exception.auth;

import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;

public class AuthTokenBeforeExpiredException extends ApiException {

    public AuthTokenBeforeExpiredException() {
        super(ApiErrorCode.O_AUTH_TOKEN_BEFORE_EXPIRED);
    }
}
