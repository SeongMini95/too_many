package com.toomany.domain.usertoken;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTokenTest {

    @Nested
    class reissue {

        @Test
        void 토큰을_재발급한다() {
            // given
            UserToken userToken = UserToken.builder()
                    .refreshToken("refreshToken")
                    .expireDatetime(LocalDateTime.now())
                    .build();

            String newRefreshToken = RandomStringUtils.randomAlphanumeric(128);

            // when
            userToken.reissue(newRefreshToken, 60 * 60 * 24 * 7);

            // then
            assertThat(userToken.getRefreshToken()).isEqualTo(newRefreshToken);
            assertThat(userToken.getExpireDatetime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(7));
        }
    }
}