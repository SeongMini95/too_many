package com.ojeomme.common.jwt.handler;

import com.ojeomme.common.jwt.entity.AuthToken;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTokenProviderTest {

    private static final String SECRET_KEY = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Nested
    class createAuthToken {

        @Test
        void 토큰을_생성한다() {
            // given
            AuthTokenProvider authTokenProvider = new AuthTokenProvider(SECRET_KEY, 60 * 30 * 1000);
            Long userId = 1L;

            // when
            AuthToken authToken = authTokenProvider.createAuthToken(userId);

            // then
            assertThat(authToken.getToken()).isNotNull();
        }
    }

    @Nested
    class convertAuthToken {

        @Test
        void accessToken을_AuthToken으로_변환한다() {
            // given
            AuthTokenProvider authTokenProvider = new AuthTokenProvider(SECRET_KEY, 60 * 30 * 1000);
            Long userId = 1L;

            String accessToken = authTokenProvider.createAuthToken(userId).getToken();

            // when
            AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);

            // then
            assertThat(authToken.getUserId()).isEqualTo(userId);
        }
    }
}