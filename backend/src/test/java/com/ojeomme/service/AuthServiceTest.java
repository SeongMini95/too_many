package com.ojeomme.service;

import com.ojeomme.common.jwt.entity.AuthToken;
import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.common.oauth.handler.OauthClientHandler;
import com.ojeomme.common.oauth.info.OauthUserInfo;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.domain.usertoken.UserToken;
import com.ojeomme.domain.usertoken.repository.UserTokenRepository;
import com.ojeomme.dto.request.auth.ReissueTokenRequestDto;
import com.ojeomme.dto.response.auth.LoginCheckResponseDto;
import com.ojeomme.dto.response.auth.LoginTokenResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import com.ojeomme.exception.auth.AuthTokenBeforeExpiredException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private OauthClientHandler oauthClientHandler;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthTokenProvider authTokenProvider;

    @Mock
    private UserTokenRepository userTokenRepository;

    private static final String REDIRECT_URI = "http://localhost:3000/auth/";

    @Nested
    class getLoginUri {

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 로그인_uri를_반환한다(OauthProvider oauthProvider) {
            // given
            String provider = oauthProvider.name().toLowerCase();
            String redirectUri = REDIRECT_URI + provider + "/login";

            switch (oauthProvider) {
                case KAKAO:
                    given(oauthClientHandler.getLoginUri(eq(oauthProvider), eq(redirectUri))).willReturn("https://kauth.kakao.com/oauth/authorize?redirect_uri=" + redirectUri);
                    break;
                case NAVER:
                    given(oauthClientHandler.getLoginUri(eq(oauthProvider), eq(redirectUri))).willReturn("https://nid.naver.com/oauth2.0/authorize?redirect_uri=" + redirectUri);
            }

            // when
            String loginUri = authService.getLoginUri(provider, redirectUri);

            // then
            assertThat(loginUri).contains(redirectUri);
        }
    }

    @Nested
    class login {

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 존재하는_유저의_로그인하여_토큰을_반환한다(OauthProvider oauthProvider) {
            // given
            String oauthId = "12345";
            String email = "mock123@email.com";

            OauthUserInfo mockOauthUserInfo = mock(OauthUserInfo.class);
            given(oauthClientHandler.getOauthUserInfo(any(OauthProvider.class), anyString(), anyString())).willReturn(mockOauthUserInfo);
            given(mockOauthUserInfo.getId()).willReturn(oauthId);
            given(mockOauthUserInfo.getEmail()).willReturn(email);

            User mockUser = mock(User.class);
            given(userRepository.findByOauthIdAndOauthProvider(eq(oauthId), any(OauthProvider.class))).willReturn(Optional.of(mockUser));

            AuthToken mockAuthToken = mock(AuthToken.class);
            String accessToken = "accessToken";
            given(authTokenProvider.createAuthToken(anyLong())).willReturn(mockAuthToken);
            given(mockAuthToken.getToken()).willReturn(accessToken);

            given(userTokenRepository.save(any(UserToken.class))).willReturn(mock(UserToken.class));

            // when
            LoginTokenResponseDto responseDto = authService.login(oauthProvider.name().toLowerCase(), "redirectUri", "code");

            // then
            assertThat(responseDto.getAccessToken()).isEqualTo(accessToken);
            assertThat(responseDto.getRefreshToken()).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(OauthProvider.class)
        void 존재하지_않은_유저의_로그인하여_토큰을_반환한다(OauthProvider oauthProvider) {
            // given
            String oauthId = "12345";
            String email = "mock123@email.com";

            OauthUserInfo mockOauthUserInfo = mock(OauthUserInfo.class);
            given(oauthClientHandler.getOauthUserInfo(any(OauthProvider.class), anyString(), anyString())).willReturn(mockOauthUserInfo);
            given(mockOauthUserInfo.getId()).willReturn(oauthId);
            given(mockOauthUserInfo.getEmail()).willReturn(email);

            given(userRepository.findByOauthIdAndOauthProvider(eq(oauthId), any(OauthProvider.class))).willReturn(Optional.empty());

            User mockUser = mock(User.class);
            given(userRepository.save(any(User.class))).willReturn(mockUser);

            AuthToken mockAuthToken = mock(AuthToken.class);
            String accessToken = "accessToken";
            given(authTokenProvider.createAuthToken(anyLong())).willReturn(mockAuthToken);
            given(mockAuthToken.getToken()).willReturn(accessToken);

            given(userTokenRepository.save(any(UserToken.class))).willReturn(mock(UserToken.class));

            // when
            LoginTokenResponseDto responseDto = authService.login(oauthProvider.name().toLowerCase(), "redirectUri", "code");

            // then
            assertThat(responseDto.getAccessToken()).isEqualTo(accessToken);
            assertThat(responseDto.getRefreshToken()).isNotNull();
        }
    }

    @Nested
    class reissue {

        @Test
        void 토큰을_재발급한다() {
            // given
            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(RandomStringUtils.randomAlphanumeric(128))
                    .build();

            MockHttpServletRequest request = new MockHttpServletRequest();
            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            Long mockUserId = 1L;
            given(mockAuthToken.getExpiredUserId()).willReturn(mockUserId);

            UserToken mockUserToken = mock(UserToken.class);
            given(userTokenRepository.findByUserIdAndRefreshTokenAndExpireDatetimeAfter(eq(mockUserId), eq(requestDto.getRefreshToken()), any(LocalDateTime.class))).willReturn(Optional.of(mockUserToken));

            String newAccessToken = "newAccessToken";
            AuthToken mockNewAuthToken = mock(AuthToken.class);
            given(authTokenProvider.createAuthToken(eq(mockUserId))).willReturn(mockNewAuthToken);
            given(mockNewAuthToken.getToken()).willReturn(newAccessToken);

            // when
            LoginTokenResponseDto responseDto = authService.reissue(request, requestDto);

            // then
            assertThat(responseDto.getAccessToken()).isEqualTo(newAccessToken);
            assertThat(responseDto.getRefreshToken()).isNotEqualTo(requestDto.getRefreshToken());
        }

        @Test
        void 토큰을_재발급하는데_AccessToken을_추출하지_못하면_UnauthorizedException을_발생한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(RandomStringUtils.randomAlphanumeric(128))
                    .build();

            // when
            ApiException exception = assertThrows(ApiException.class, () -> authService.reissue(request, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.UNAUTHORIZED);
        }

        @Test
        void 만료되지_않은_토큰은_재발급하면_유저토큰을_삭제하고_AuthTokenBeforeExpiredException를_발생한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(RandomStringUtils.randomAlphanumeric(128))
                    .build();

            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            Long userId = 1L;
            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(true);

            given(mockAuthToken.getUserId()).willReturn(userId);
            given(userTokenRepository.findByUserId(eq(userId))).willReturn(List.of());

            // when, then
            assertThatThrownBy(() -> authService.reissue(request, requestDto)).isInstanceOf(AuthTokenBeforeExpiredException.class);

            then(userTokenRepository).should(times(1)).deleteAll(anyList());
        }

        @Test
        void 토큰을_재발급하는데_회원_id를_가져올_수_없으면_UserNotFoundException를_발생한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(RandomStringUtils.randomAlphanumeric(128))
                    .build();

            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            given(mockAuthToken.getExpiredUserId()).willReturn(null);

            // when
            ApiException exception = assertThrows(ApiException.class, () -> authService.reissue(request, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 토큰을_재발급하는데_디비에서_유저토큰을_찾지_못하면_UserTokenNotFoundException를_발생한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            ReissueTokenRequestDto requestDto = ReissueTokenRequestDto.builder()
                    .refreshToken(RandomStringUtils.randomAlphanumeric(128))
                    .build();

            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            Long mockUserId = 1L;
            given(mockAuthToken.getExpiredUserId()).willReturn(mockUserId);

            given(userTokenRepository.findByUserIdAndRefreshTokenAndExpireDatetimeAfter(anyLong(), anyString(), any(LocalDateTime.class))).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> authService.reissue(request, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_TOKEN_NOT_FOUND);
        }
    }

    @Nested
    class check {

        @Test
        void 로그인_상태를_검증한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(true);

            User mockUser = User.builder()
                    .nickname("nickname")
                    .profile("profile")
                    .build();
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mockUser));

            // when
            LoginCheckResponseDto responseDto = authService.check(request);

            // then
            assertThat(responseDto.isResult()).isTrue();
            assertThat(responseDto.getNickname()).isEqualTo(mockUser.getNickname());
            assertThat(responseDto.getProfile()).isEqualTo(mockUser.getProfile());
        }

        @Test
        void 로그인_상태를_검증한는데_accessToken이_없으면_false를_리턴한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();

            // when
            LoginCheckResponseDto responseDto = authService.check(request);

            // then
            assertThat(responseDto.isResult()).isFalse();
        }

        @Test
        void 로그인_상태를_검증하는데_토큰이_검증이_안되면_false를_리턴한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(false);

            // when
            LoginCheckResponseDto responseDto = authService.check(request);

            // then
            assertThat(responseDto.isResult()).isFalse();
        }

        @Test
        void 로그인_상태를_검증하는데_유저_아이디가_없으면_false를_리턴한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(true);

            given(mockAuthToken.getUserId()).willReturn(null);

            // when
            LoginCheckResponseDto responseDto = authService.check(request);

            // then
            assertThat(responseDto.isResult()).isFalse();
        }

        @Test
        void 로그인_상태를_검증하는데_유저를_찾을수_없으면_false를_리턴한다() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            String accessToken = "accessToken";
            request.addHeader("Authorization", "Bearer " + accessToken);

            AuthToken mockAuthToken = mock(AuthToken.class);
            given(authTokenProvider.convertAuthToken(eq(accessToken))).willReturn(mockAuthToken);
            given(mockAuthToken.validate()).willReturn(true);

            Long mockUserId = 1L;
            given(mockAuthToken.getUserId()).willReturn(mockUserId);

            given(userRepository.findById(eq(mockUserId))).willReturn(Optional.empty());

            // when
            LoginCheckResponseDto responseDto = authService.check(request);

            // then
            assertThat(responseDto.isResult()).isFalse();
        }
    }
}