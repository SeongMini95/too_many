package com.toomany.common.jwt.entity;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
public class AuthToken {

    private final Key key;

    @Getter
    private final String token;

    private static final String ID = "id";

    public AuthToken(Key key, Long userId, long expiryMilliseconds) {
        this.key = key;
        this.token = createAuthToken(userId, expiryMilliseconds);
    }

    private String createAuthToken(Long userId, long expiryMilliseconds) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMilliseconds);

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .claim(ID, userId)
                .setExpiration(expiry)
                .compact();
    }

    public boolean validate() {
        return getUserId() != null;
    }

    public Long getUserId() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get(ID, Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getExpiredUserId() {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return null;
        } catch (ExpiredJwtException e) {
            return e.getClaims().get(ID, Long.class);
        } catch (Exception e) {
            return null;
        }
    }
}
