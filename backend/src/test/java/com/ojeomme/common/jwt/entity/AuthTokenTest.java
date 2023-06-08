package com.ojeomme.common.jwt.entity;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTokenTest {

    private static final String SECRET_KEY = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Nested
    class createAuthToken {

        @Test
        void 토큰을_생선한다() {
            // given
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Long userId = 1L;
            long expiryMilliseconds = 60 * 30 * 1000;

            // when
            AuthToken authToken = new AuthToken(key, userId, expiryMilliseconds);

            // then
            assertThat(authToken.getToken()).isNotNull();
        }
    }

    @Nested
    class getUserId {

        @Test
        void 토큰에서_유저_아이디를_가져온다() {
            // given
            Long mockUserId = 1L;
            AuthToken authToken = createAuthToken(mockUserId);

            // when
            Long userId = authToken.getUserId();

            // then
            assertThat(userId).isEqualTo(mockUserId);
        }

        @Test
        void 유저의_아이디를_가져오는데_토큰이_비정상_상태이면_null을_반환한다() {
            // given
            Long userId = 1L;
            AuthToken expiredAuthToken = createExpiredAuthToken(userId);
            AuthToken wrongAuthToken = createWrongAuthToken();

            // when
            Long expiredUserId = expiredAuthToken.getUserId();
            Long wrongUserId = wrongAuthToken.getUserId();

            // then
            assertThat(expiredUserId).isNull();
            assertThat(wrongUserId).isNull();
        }
    }

    @Nested
    class validate {

        @Test
        void 토큰을_검증한다() {
            // given
            Long userId = 1L;
            AuthToken authToken = createAuthToken(userId);
            AuthToken expiredAuthToken = createExpiredAuthToken(userId);

            // when
            boolean validTrue = authToken.validate();
            boolean validFalse = expiredAuthToken.validate();

            // then
            assertThat(validTrue).isTrue();
            assertThat(validFalse).isFalse();
        }
    }

    @Nested
    class getExpiredUserId {

        @Test
        void 만료된_토큰에서_유저의_아이디를_가져온다() {
            // given
            Long mockUserId = 1L;
            AuthToken expiredAuthToken = createExpiredAuthToken(mockUserId);

            // when
            Long userId = expiredAuthToken.getExpiredUserId();

            // then
            assertThat(userId).isEqualTo(mockUserId);
        }

        @Test
        void 만료되지_않은_토큰에서_유저의_아이디를_가져오면_null을_반환한다() {
            // given
            Long mockUserId = 1L;
            AuthToken authToken = createAuthToken(mockUserId);
            AuthToken wrongAuthToken = createWrongAuthToken();

            // when
            Long correctUserId = authToken.getExpiredUserId();
            Long wrongUserId = wrongAuthToken.getExpiredUserId();

            // then
            assertThat(correctUserId).isNull();
            assertThat(wrongUserId).isNull();
        }
    }

    private AuthToken createAuthToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        long expiryMilliseconds = 1800000;

        return new AuthToken(key, userId, expiryMilliseconds);
    }

    private AuthToken createExpiredAuthToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        long expiryMilliseconds = 0;

        return new AuthToken(key, userId, expiryMilliseconds);
    }

    private AuthToken createWrongAuthToken() {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return new AuthToken(key, "wrong");
    }
}