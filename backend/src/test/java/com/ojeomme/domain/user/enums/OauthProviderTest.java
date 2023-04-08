package com.ojeomme.domain.user.enums;

import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OauthProviderTest {

    @Nested
    class of {

        @Test
        void name에_따른_enum을_반환한다() {
            // given
            String name = OauthProvider.KAKAO.name().toLowerCase();

            // when
            OauthProvider oauthProvider = OauthProvider.of(name);

            // then
            assertThat(oauthProvider).isEqualTo(OauthProvider.KAKAO);
        }

        @Test
        void 맞는_name이_없으면_NotSupportOauthProviderException을_발생한다() {
            // given
            String name = "google";

            // when
            ApiException exception = assertThrows(ApiException.class, () -> OauthProvider.of(name));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.NOT_SUPPORT_OAUTH_PROVIDER);
        }
    }
}