package com.toomany.controller;

import com.toomany.common.jwt.entity.AuthToken;
import com.toomany.common.jwt.handler.AuthTokenProvider;
import com.toomany.common.oauth.client.KakaoOauthClient;
import com.toomany.common.oauth.client.NaverOauthClient;
import com.toomany.controller.support.AcceptanceTest;
import com.toomany.domain.user.User;
import com.toomany.domain.user.enums.OauthProvider;
import com.toomany.domain.user.repository.UserRepository;
import com.toomany.domain.usertoken.UserToken;
import com.toomany.domain.usertoken.repository.UserTokenRepository;
import com.toomany.dto.request.auth.ReissueTokenRequestDto;
import com.toomany.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthControllerTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @SpyBean
    private NaverOauthClient naverOauthClient;

    @SpyBean
    private KakaoOauthClient kakaoOauthClient;

    @SpyBean
    private AuthTokenProvider authTokenProvider;

    private MockWebServer mockWebServer;

    private static final String O_AUTH_ID = "32742776";
    private static final String EMAIL = "test123@naver.com";
    private static final String NICKNAME = "홍길동";
    private static final String NAVER_AUTHORIZATION_RESPONSE = "{\n" +
            "    \"access_token\":\"AAAAQosjWDJieBiQZc3to9YQp6HDLvrmyKC+6+iZ3gq7qrkqf50ljZC+Lgoqrg\",\n" +
            "    \"refresh_token\":\"c8ceMEJisO4Se7uGisHoX0f5JEii7JnipglQipkOn5Zp3tyP7dHQoP0zNKHUq2gY\",\n" +
            "    \"token_type\":\"bearer\",\n" +
            "    \"expires_in\":\"3600\"\n" +
            "}";
    private static final String NAVER_USER_INFO_RESPONSE = "{\n" +
            "  \"resultcode\": \"00\",\n" +
            "  \"message\": \"success\",\n" +
            "  \"response\": {\n" +
            "    \"id\": \"" + O_AUTH_ID + "\",\n" +
            "    \"email\": \"" + EMAIL + "\",\n" +
            "    \"nickname\": \"" + NICKNAME + "\"\n" +
            "  }\n" +
            "}";
    private static final String KAKAO_AUTHORIZATION_RESPONSE = "{\n" +
            "    \"token_type\":\"bearer\",\n" +
            "    \"access_token\":\"accessToken\",\n" +
            "    \"expires_in\":43199,\n" +
            "    \"refresh_token\":\"refreshToken\",\n" +
            "    \"refresh_token_expires_in\":25184000\n" +
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
        void 로그인_uri를_반환한다(OauthProvider oauthProvider) {
            // given
            String provider = oauthProvider.name().toLowerCase();
            String redirectUri = "http://localhost:3000/auth/" + provider + "/login";

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .queryParam("redirectUri", redirectUri)
                    .when().get("/api/auth/{provider}/login/uri", provider)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.asString()).contains(redirectUri);
        }
    }

    @Nested
    class login {

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 존재하는_유저의_로그인하여_토큰을_반환한다(OauthProvider oauthProvider) throws IOException {
            mockWebServer = new MockWebServer();
            mockWebServer.start();

            setMockWebServer(oauthProvider);

            userRepository.save(User.builder()
                    .oauthId(O_AUTH_ID)
                    .oauthProvider(oauthProvider)
                    .email(EMAIL)
                    .nickname(NICKNAME)
                    .profile("")
                    .build());

            String provider = oauthProvider.name().toLowerCase();
            String redirectUri = "http://localhost:3000/auth/" + provider + "/login";
            String code = "code";

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .queryParam("redirectUri", redirectUri)
                    .queryParam("code", code)
                    .when().get("/api/auth/{provider}/login", provider)
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("accessToken")).isNotNull();
            assertThat(jsonPath.getString("refreshToken")).isNotNull();

            mockWebServer.shutdown();
        }

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 존재하지_않은_유저의_로그인하여_토큰을_반환한다(OauthProvider oauthProvider) throws IOException {
            mockWebServer = new MockWebServer();
            mockWebServer.start();

            setMockWebServer(oauthProvider);

            String provider = oauthProvider.name().toLowerCase();
            String redirectUri = "http://localhost:3000/auth/" + provider + "/login";
            String code = "code";

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .queryParam("redirectUri", redirectUri)
                    .queryParam("code", code)
                    .when().get("/api/auth/{provider}/login", provider)
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("accessToken")).isNotNull();
            assertThat(jsonPath.getString("refreshToken")).isNotNull();

            mockWebServer.shutdown();
        }
    }

    @Nested
    class reissue {

        @Test
        void 토큰을_재발급한다() {
            // given
            UserToken userToken = insertUserToken(user);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            given(mockAuthToken.getExpiredUserId()).willReturn(user.getId());

            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(userToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/auth/reissue")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("accessToken")).isNotBlank();
            assertThat(jsonPath.getString("refreshToken")).isNotBlank();
        }

        @Test
        void 토큰을_재발급하는데_AccessToken을_추출하지_못하면_UnauthorizedException을_발생한다() {
            // given
            UserToken userToken = insertUserToken(user);

            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(userToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/auth/reissue")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.UNAUTHORIZED.getHttpStatus().value());
        }

        @Test
        void 만료되지_않은_토큰은_재발급하면_유저토큰을_삭제하고_AuthTokenBeforeExpiredException를_발생한다() {
            // given
            UserToken userToken = insertUserToken(user);

            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(userToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/auth/reissue")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.O_AUTH_TOKEN_BEFORE_EXPIRED.getHttpStatus().value());
        }

        @Test
        void 토큰을_재발급하는데_회원_id를_가져올_수_없으면_UserIdExtractFailedException를_발생한다() {
            // given
            UserToken userToken = insertUserToken(user);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            given(mockAuthToken.getExpiredUserId()).willReturn(null);

            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(userToken.getRefreshToken())
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/auth/reissue")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
        }

        @Test
        void 토큰을_재발급하는데_디비에서_유저토큰을_찾지_못하면_UserTokenNotFoundException를_발생한다() {
            // given
            insertUserToken(user);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            given(mockAuthToken.getExpiredUserId()).willReturn(user.getId());

            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken("wrong")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/auth/reissue")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_TOKEN_NOT_FOUND.getHttpStatus().value());
        }
    }

    @Nested
    class check {

        @Test
        void 로그인_상태를_검증한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/api/auth/check")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getBoolean("result")).isTrue();
            assertThat(jsonPath.getString("nickname")).isEqualTo(user.getNickname());
            assertThat(jsonPath.getString("profile")).isEqualTo(user.getProfile());
        }

        @Test
        void 로그인_상태를_검증한는데_accessToken이_없으면_false를_리턴한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/api/auth/check")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getBoolean("result")).isFalse();
        }

        @Test
        void 로그인_상태를_검증하는데_토큰이_검증이_안되면_false를_리턴한다() {
            // given
            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(accessToken)).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/api/auth/check")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getBoolean("result")).isFalse();
        }

        @Test
        void 로그인_상태를_검증하는데_유저_아이디가_없으면_false를_리턴한다() {
            // given
            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(accessToken)).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(true);

            given(mockAuthToken.getUserId()).willReturn(null);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/api/auth/check")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getBoolean("result")).isFalse();
        }

        @Test
        void 로그인_상태를_검증하는데_유저를_찾을수_없으면_false를_리턴한다() {
            // given
            userRepository.delete(user);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/api/auth/check")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getBoolean("result")).isFalse();
        }
    }

    private void setMockWebServer(OauthProvider oauthProvider) {
        String baseUri = String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort());

        switch (oauthProvider) {
            case NAVER:
                ReflectionTestUtils.setField(naverOauthClient, "tokenClient", WebClient.create().mutate().baseUrl(baseUri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());
                ReflectionTestUtils.setField(naverOauthClient, "userInfoClient", WebClient.create().mutate().baseUrl(baseUri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

                mockWebServer.enqueue(new MockResponse()
                        .setBody(NAVER_AUTHORIZATION_RESPONSE)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

                mockWebServer.enqueue(new MockResponse()
                        .setBody(NAVER_USER_INFO_RESPONSE)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
                break;
            case KAKAO:
                ReflectionTestUtils.setField(kakaoOauthClient, "tokenClient", WebClient.create().mutate().baseUrl(baseUri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());
                ReflectionTestUtils.setField(kakaoOauthClient, "userInfoClient", WebClient.create().mutate().baseUrl(baseUri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

                mockWebServer.enqueue(new MockResponse()
                        .setBody(KAKAO_AUTHORIZATION_RESPONSE)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

                mockWebServer.enqueue(new MockResponse()
                        .setBody(KAKAO_USER_INFO_RESPONSE)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        }
    }

    private UserToken insertUserToken(User user) {
        return userTokenRepository.save(UserToken.builder()
                .user(user)
                .refreshToken(RandomStringUtils.randomAlphanumeric(128))
                .expireDatetime(LocalDateTime.now().plusDays(7))
                .build());
    }
}