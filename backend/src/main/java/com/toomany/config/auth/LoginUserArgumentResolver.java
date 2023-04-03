package com.toomany.config.auth;

import com.toomany.common.jwt.entity.AuthToken;
import com.toomany.common.jwt.handler.AccessTokenExtractor;
import com.toomany.common.jwt.handler.AuthTokenProvider;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthTokenProvider authTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String accessToken = AccessTokenExtractor.extract(request).orElseThrow(() -> new ApiException(ApiErrorCode.UNAUTHORIZED));
        AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);

        Long userId = authToken.getUserId();
        if (userId == null) {
            throw new ApiException(ApiErrorCode.UNAUTHORIZED);
        }

        return userId;
    }
}
