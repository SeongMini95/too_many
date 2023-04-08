package com.ojeomme.common.jwt.handler;

import com.ojeomme.common.jwt.entity.AuthToken;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.security.Key;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenExtractorTest {

    @Nested
    class extract {

        @Test
        void 토큰을_추출한다() {
            // given
            String accessToken = createAuthToken().getToken();

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer " + accessToken);

            // when
            String getAccessToken = AccessTokenExtractor.extract(request).orElse(null);

            // then
            assertThat(getAccessToken).isEqualTo(accessToken);
        }

        @Test
        void 헤더가_없는_상태에서_추출하면_null을_반환한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();

            // when
            String accessToken = AccessTokenExtractor.extract(request).orElse(null);

            // then
            assertThat(accessToken).isNull();
        }

        @Test
        void 헤더는_존재하지만_토큰이_존재하지_않으면_AccessTokenExtractException를_발생한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "test");

            // when
            String accessToken = AccessTokenExtractor.extract(request).orElse(null);

            // then
            assertThat(accessToken).isNull();
        }
    }

    private AuthToken createAuthToken() {
        Key key = Keys.hmacShaKeyFor("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes());
        long expiryMilliseconds = 1800000;

        return new AuthToken(key, 1L, expiryMilliseconds);
    }
}