package com.toomany.common.oauth.client;

import com.toomany.common.oauth.info.OauthUserInfo;
import com.toomany.domain.user.enums.OauthProvider;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KakaoOauthClientTest {

    private MockWebServer mockWebServer;

    private static final String REDIRECT_URI = "http://localhost:3000/auth/kakao/login";
    private static final String O_AUTH_ID = "32742776";
    private static final String EMAIL = "test123@naver.com";
    private static final String NICKNAME = "홍길동";
    private static final String AUTHORIZATION_RESPONSE = "{\n" +
            "    \"token_type\":\"bearer\",\n" +
            "    \"access_token\":\"accessToken\",\n" +
            "    \"expires_in\":43199,\n" +
            "    \"refresh_token\":\"refreshToken\",\n" +
            "    \"refresh_token_expires_in\":25184000\n" +
            "}";
    private static final String USER_INFO_RESPONSE = "{\n" +
            "    \"id\":\"" + O_AUTH_ID + "\",\n" +
            "    \"connected_at\":\"2022-04-11T01:45:28Z\",\n" +
            "    \"kakao_account\":{\n" +
            "        \"email\":\"" + EMAIL + "\",\n" +
            "        \"profile\":{\n" +
            "             \"nickname\":\"" + NICKNAME + "\"\n" +
            "        }\n" +
            "    }\n" +
            "}";
    private static final String ERROR_RESPONSE = "{\n" +
            "    \"error\": \"invalid_token\",\n" +
            "    \"error_description\": \"ID 토큰 값이 전달되지 않았거나 올바른 형식이 아닌 ID 토큰입니다.\",\n" +
            "    \"error_code\": \"KOE400\"\n" +
            "}";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    class support {

        @Test
        void 카카오_플랫폼이_들어오면_true를_반환한다() {
            // given
            KakaoOauthClient kakaoOauthClient = new KakaoOauthClient(
                    "clientId",
                    "secretId",
                    "https://kauth.kakao.com/oauth/authorize",
                    "https://kauth.kakao.com/oauth/token",
                    "https://kapi.kakao.com/v2/user/me",
                    WebClient.create()
            );

            // when
            boolean isNaver = kakaoOauthClient.support(OauthProvider.NAVER);
            boolean isKakao = kakaoOauthClient.support(OauthProvider.KAKAO);

            // then
            assertThat(isNaver).isFalse();
            assertThat(isKakao).isTrue();
        }
    }

    @Nested
    class getLoginUri {

        @Test
        void 카카오_플랫폼_OAuth2에_접근하여_code를_얻고_uri를_반환한다() {
            // given
            KakaoOauthClient kakaoOauthClient = new KakaoOauthClient(
                    "clientId",
                    "secretId",
                    "https://kauth.kakao.com/oauth/authorize",
                    "https://kauth.kakao.com/oauth/token",
                    "https://kapi.kakao.com/v2/user/me",
                    WebClient.create()
            );

            // when
            String authUri = kakaoOauthClient.getLoginUri(REDIRECT_URI);

            // then
            assertThat(authUri).contains(REDIRECT_URI);
        }
    }

    @Nested
    class getUserInfo {

        @Test
        void 사용자의_정보를_가져온다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(AUTHORIZATION_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            mockWebServer.enqueue(new MockResponse()
                    .setBody(USER_INFO_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoOauthClient kakaoOauthClient = new KakaoOauthClient(
                    "clientId",
                    "secretId",
                    "https://kauth.kakao.com/oauth/authorize",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            OauthUserInfo oauthUserInfo = kakaoOauthClient.getUserInfo(REDIRECT_URI, "code");

            // then
            assertThat(oauthUserInfo.getId()).isEqualTo(O_AUTH_ID);
            assertThat(oauthUserInfo.getEmail()).isEqualTo(EMAIL);
            assertThat(oauthUserInfo.getNickname()).isEqualTo(NICKNAME);
        }

        @Test
        void 토큰_발급시_에러발생시_오류를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setStatus("HTTP/1.1 500")
                    .setBody(ERROR_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoOauthClient kakaoOauthClient = new KakaoOauthClient(
                    "clientId",
                    "secretId",
                    "redirectUrl",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoOauthClient.getUserInfo(REDIRECT_URI, "code"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SOCIAL_LOGIN);
        }

        @Test
        void 토큰_발급시_Response_Data가_올바르지_않으면_오류를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody("")
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoOauthClient kakaoOauthClient = new KakaoOauthClient(
                    "clientId",
                    "secretId",
                    "redirectUrl",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoOauthClient.getUserInfo(REDIRECT_URI, "code"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SOCIAL_LOGIN);
        }

        @Test
        void 사용자_정보_가져오기시_Response_Data가_올바르지_않으면_오류를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(AUTHORIZATION_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            mockWebServer.enqueue(new MockResponse()
                    .setBody("")
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoOauthClient kakaoOauthClient = new KakaoOauthClient(
                    "clientId",
                    "secretId",
                    "redirectUrl",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoOauthClient.getUserInfo(REDIRECT_URI, "code"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SOCIAL_LOGIN);
        }
    }
}