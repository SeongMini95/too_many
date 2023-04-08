package com.ojeomme.config.auth;

import com.ojeomme.common.jwt.entity.AuthToken;
import com.ojeomme.common.jwt.handler.AccessTokenExtractor;
import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final AuthTokenProvider authTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        try {
            String accessToken = AccessTokenExtractor.extract(request).orElseThrow(() -> new ApiException(ApiErrorCode.UNAUTHORIZED));
            AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);

            if (!authToken.validate()) {
                throw new ApiException(ApiErrorCode.UNAUTHORIZED);
            }
        } catch (ApiException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ApiErrorCode.UNAUTHORIZED.getMessage());
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
