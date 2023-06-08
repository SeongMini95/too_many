package com.ojeomme.common.jwt.handler;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Optional;

@UtilityClass
public class AccessTokenExtractor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";

    public static Optional<String> extract(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.startsWith(BEARER)) {
                return Optional.of(value.split(" ")[1]);
            }
        }

        return Optional.empty();
    }
}
