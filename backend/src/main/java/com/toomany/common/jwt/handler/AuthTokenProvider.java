package com.toomany.common.jwt.handler;

import com.toomany.common.jwt.entity.AuthToken;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class AuthTokenProvider {

    private final Key key;
    private final long expiryMilliseconds;

    public AuthTokenProvider(@Value("${security.jwt.token.secret-key}") String secretKey,
                             @Value("${security.jwt.token.expiry}") long expiryMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expiryMilliseconds = expiryMilliseconds;
    }

    public AuthToken createAuthToken(Long userId) {
        return new AuthToken(key, userId, expiryMilliseconds);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(key, token);
    }
}
