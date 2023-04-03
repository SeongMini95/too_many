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

class NaverOauthClientTest {

    private MockWebServer mockWebServer;

    private static final String REDIRECT_URI = "http://localhost:3000/auth/naver/login";
    private static final String O_AUTH_ID = "32742776";
    private static final String EMAIL = "test123@naver.com";
    private static final String NICKNAME = "홍길동";
    private static final String AUTHORIZATION_RESPONSE = "{\n" +
            "    \"access_token\":\"AAAAQosjWDJieBiQZc3to9YQp6HDLvrmyKC+6+iZ3gq7qrkqf50ljZC+Lgoqrg\",\n" +
            "    \"refresh_token\":\"c8ceMEJisO4Se7uGisHoX0f5JEii7JnipglQipkOn5Zp3tyP7dHQoP0zNKHUq2gY\",\n" +
            "    \"token_type\":\"bearer\",\n" +
            "    \"expires_in\":\"3600\"\n" +
            "}";
    private static final String USER_INFO_RESPONSE = "{\n" +
            "  \"resultcode\": \"00\",\n" +
            "  \"message\": \"success\",\n" +
            "  \"response\": {\n" +
            "    \"id\": \"" + O_AUTH_ID + "\",\n" +
            "    \"email\": \"" + EMAIL + "\",\n" +
            "    \"nickname\": \"" + NICKNAME + "\"\n" +
            "  }\n" +
            "}";
    private static final String ERROR_RESPONSE = "{\n" +
            "   \"error\": \"unauthorized_client\",\n" +
            "   \"error_description\": \"인증받지 않은 인증 코드(authorization code)로 요청했습니다.\"\n" +
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
        void 네이버_플랫폼이_들어오면_true를_반환한다() {
            // given
            NaverOauthClient naverOauthClient = new NaverOauthClient(
                    "clientId",
                    "secretId",
                    "https://nid.naver.com/oauth2.0/authorize",
                    "https://nid.naver.com/oauth2.0/token",
                    "https://openapi.naver.com/v1/nid/me",
                    WebClient.create()
            );

            // when
            boolean isNaver = naverOauthClient.support(OauthProvider.NAVER);
            boolean isKakao = naverOauthClient.support(OauthProvider.KAKAO);

            // then
            assertThat(isNaver).isTrue();
            assertThat(isKakao).isFalse();
        }
    }

    @Nested
    class getLoginUri {

        @Test
        void 네이버_OAuth2에_접근하여_code를_얻고_uri을_반환한다() {
            // given
            NaverOauthClient naverOauthClient = new NaverOauthClient(
                    "clientId",
                    "secretId",
                    "https://nid.naver.com/oauth2.0/authorize",
                    "https://nid.naver.com/oauth2.0/token",
                    "https://openapi.naver.com/v1/nid/me",
                    WebClient.create()
            );

            // when
            String authUri = naverOauthClient.getLoginUri(REDIRECT_URI);

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

            NaverOauthClient naverOauthClient = new NaverOauthClient(
                    "clientId",
                    "secretId",
                    "redirectUrl",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            OauthUserInfo oauthUserInfo = naverOauthClient.getUserInfo(REDIRECT_URI, "code");

            // then
            assertThat(oauthUserInfo.getId()).isEqualTo(O_AUTH_ID);
            assertThat(oauthUserInfo.getEmail()).isEqualTo(EMAIL);
            assertThat(oauthUserInfo.getNickname()).isEqualTo(NICKNAME);
        }

        @Test
        void 토큰_발급시_에러발생시_오류를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(ERROR_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            NaverOauthClient naverOauthClient = new NaverOauthClient(
                    "clientId",
                    "secretId",
                    "redirectUrl",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> naverOauthClient.getUserInfo(REDIRECT_URI, "code"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SOCIAL_LOGIN);
        }

        @Test
        void 토큰_발급시_Response_Data가_올바르지_않으면_오류를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody("")
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            NaverOauthClient naverOauthClient = new NaverOauthClient(
                    "clientId",
                    "secretId",
                    "redirectUrl",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> naverOauthClient.getUserInfo(REDIRECT_URI, "code"));

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

            NaverOauthClient naverOauthClient = new NaverOauthClient(
                    "clientId",
                    "secretId",
                    "redirectUrl",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> naverOauthClient.getUserInfo(REDIRECT_URI, "code"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SOCIAL_LOGIN);
        }
    }
}