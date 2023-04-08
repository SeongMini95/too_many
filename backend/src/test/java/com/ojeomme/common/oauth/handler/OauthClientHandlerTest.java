package com.ojeomme.common.oauth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojeomme.common.oauth.client.KakaoOauthClient;
import com.ojeomme.common.oauth.client.NaverOauthClient;
import com.ojeomme.common.oauth.info.KakaoOauthUserInfo;
import com.ojeomme.common.oauth.info.NaverOauthUserInfo;
import com.ojeomme.common.oauth.info.OauthUserInfo;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {OauthClientHandler.class, NaverOauthClient.class, KakaoOauthClient.class})
class OauthClientHandlerTest {

    @Autowired
    private OauthClientHandler oauthClientHandler;

    @MockBean
    private NaverOauthClient naverOauthClient;

    @MockBean
    private KakaoOauthClient kakaoOauthClient;

    private static final String REDIRECT_URI = "http://localhost:3000/auth/";
    private static final String O_AUTH_ID = "oAuthId";
    private static final String EMAIL = "test123@naver.com";
    private static final String NICKNAME = "홍길동";
    private static final String NAVER_USER_INFO_RESPONSE = "{\n" +
            "  \"resultcode\": \"00\",\n" +
            "  \"message\": \"success\",\n" +
            "  \"response\": {\n" +
            "    \"id\": \"" + O_AUTH_ID + "\",\n" +
            "    \"email\": \"" + EMAIL + "\",\n" +
            "    \"nickname\": \"" + NICKNAME + "\"\n" +
            "  }\n" +
            "}";
    private static final String KAKAO_USER_INFO_RESPONSE = "{\n" +
            "    \"id\":\"" + O_AUTH_ID + "\",\n" +
            "    \"connected_at\":\"2022-04-11T01:45:28Z\",\n" +
            "    \"kakao_account\":{\n" +
            "        \"email\":\"" + EMAIL + "\",\n" +
            "        \"profile\":{\n" +
            "             \"nickname\":\"" + NICKNAME + "\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Nested
    class getLoginUri {

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 로그인_url을_반환한다(OauthProvider oauthProvider) {
            // given
            given(naverOauthClient.support(eq(OauthProvider.NAVER))).willReturn(true);
            given(naverOauthClient.getLoginUri(eq(REDIRECT_URI))).willReturn("https://nid.naver.com/oauth2.0/authorize?redirectUri=" + REDIRECT_URI);

            given(kakaoOauthClient.support(eq(OauthProvider.KAKAO))).willReturn(true);
            given(kakaoOauthClient.getLoginUri(eq(REDIRECT_URI))).willReturn("https://kauth.kakao.com/oauth/authorize?redirectUri=" + REDIRECT_URI);

            // when
            String redirectUri = oauthClientHandler.getLoginUri(oauthProvider, REDIRECT_URI);

            // then
            assertThat(redirectUri).contains(REDIRECT_URI);
        }
    }

    @Nested
    class getOauthUserInfo {

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 플랫폼에_따리_유저의_정보를_가져온다(OauthProvider oauthProvider) throws Exception {
            // given
            String redirectUri = REDIRECT_URI + oauthProvider.name().toLowerCase() + "/login";

            given(naverOauthClient.support(eq(OauthProvider.NAVER))).willReturn(true);
            given(naverOauthClient.getUserInfo(eq(redirectUri), anyString())).willReturn(getNaverUserInfo());

            given(kakaoOauthClient.support(eq(OauthProvider.KAKAO))).willReturn(true);
            given(kakaoOauthClient.getUserInfo(eq(redirectUri), anyString())).willReturn(getKakaoUserInfo());

            // when
            OauthUserInfo oauthUserInfo = oauthClientHandler.getOauthUserInfo(oauthProvider, redirectUri, "code");

            // then
            assertThat(oauthUserInfo.getId()).isEqualTo(O_AUTH_ID);
            assertThat(oauthUserInfo.getEmail()).isEqualTo(EMAIL);
            assertThat(oauthUserInfo.getNickname()).isEqualTo(NICKNAME);
        }

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 지원하지_않는_플랫폼은_오류를_발생한다(OauthProvider oauthProvider) {
            // given
            String redirectUri = REDIRECT_URI + oauthProvider.name().toLowerCase() + "/login";

            given(naverOauthClient.support(eq(OauthProvider.NAVER))).willReturn(false);
            given(kakaoOauthClient.support(eq(OauthProvider.KAKAO))).willReturn(false);

            // when
            ApiException exception = assertThrows(ApiException.class, () -> oauthClientHandler.getOauthUserInfo(oauthProvider, redirectUri, "code"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.NOT_SUPPORT_OAUTH_PROVIDER);
        }

        private NaverOauthUserInfo getNaverUserInfo() throws JsonProcessingException {
            return new NaverOauthUserInfo(new ObjectMapper().readValue(NAVER_USER_INFO_RESPONSE, Map.class));
        }

        private KakaoOauthUserInfo getKakaoUserInfo() throws JsonProcessingException {
            return new KakaoOauthUserInfo(new ObjectMapper().readValue(KAKAO_USER_INFO_RESPONSE, Map.class));
        }
    }
}