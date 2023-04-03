package com.toomany.service;

import com.toomany.common.jwt.entity.AuthToken;
import com.toomany.common.jwt.handler.AccessTokenExtractor;
import com.toomany.common.jwt.handler.AuthTokenProvider;
import com.toomany.common.oauth.handler.OauthClientHandler;
import com.toomany.common.oauth.info.OauthUserInfo;
import com.toomany.domain.user.User;
import com.toomany.domain.user.enums.OauthProvider;
import com.toomany.domain.user.repository.UserRepository;
import com.toomany.domain.usertoken.UserToken;
import com.toomany.domain.usertoken.repository.UserTokenRepository;
import com.toomany.dto.request.auth.ReissueTokenRequestDto;
import com.toomany.dto.response.auth.LoginCheckResponseDto;
import com.toomany.dto.response.auth.LoginTokenResponseDto;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import com.toomany.exception.auth.AuthTokenBeforeExpiredException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final OauthClientHandler oauthClientHandler;
    private final UserRepository userRepository;
    private final AuthTokenProvider authTokenProvider;
    private final UserTokenRepository userTokenRepository;

    private static final int REFRESH_TOKEN_LENGTH = 128;
    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 7;

    @Transactional(readOnly = true)
    public String getLoginUri(String provider, String redirectUri) {
        OauthProvider oauthProvider = OauthProvider.of(provider);
        return oauthClientHandler.getLoginUri(oauthProvider, redirectUri);
    }

    @Transactional
    public LoginTokenResponseDto login(String provider, String redirectUri, String code) {
        OauthProvider oauthProvider = OauthProvider.of(provider);
        OauthUserInfo oauthUserInfo = oauthClientHandler.getOauthUserInfo(oauthProvider, redirectUri, code);

        String oauthId = oauthUserInfo.getId();
        String email = oauthUserInfo.getEmail();
        String nickname = oauthUserInfo.getNickname();

        User user = userRepository.findByOauthIdAndOauthProvider(oauthId, oauthProvider).orElse(null);
        if (user != null) {
            user.updateEmail(email);
        } else {
            user = userRepository.save(User.builder()
                    .oauthId(oauthId)
                    .oauthProvider(oauthProvider)
                    .email(email)
                    .nickname(nickname)
                    .profile("")
                    .build());
        }

        String accessToken = authTokenProvider.createAuthToken(user.getId()).getToken();
        String refreshToken = RandomStringUtils.randomAlphanumeric(REFRESH_TOKEN_LENGTH);

        userTokenRepository.save(UserToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expireDatetime(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_MAX_AGE))
                .build());

        return new LoginTokenResponseDto(accessToken, refreshToken);
    }

    @Transactional(noRollbackFor = AuthTokenBeforeExpiredException.class)
    public LoginTokenResponseDto reissue(HttpServletRequest request, ReissueTokenRequestDto requestDto) {
        String accessToken = AccessTokenExtractor.extract(request).orElseThrow(() -> new ApiException(ApiErrorCode.UNAUTHORIZED));
        String refreshToken = requestDto.getRefreshToken();

        AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);
        if (authToken.validate()) {
            Long userId = authToken.getUserId();
            userTokenRepository.deleteAll(userTokenRepository.findByUserId(userId));

            throw new AuthTokenBeforeExpiredException();
        }

        Long userId = authToken.getExpiredUserId();
        if (userId == null) {
            throw new ApiException(ApiErrorCode.USER_NOT_FOUND);
        }

        UserToken userToken = userTokenRepository.findByUserIdAndRefreshTokenAndExpireDatetimeAfter(userId, refreshToken, LocalDateTime.now())
                .orElseThrow(() -> new ApiException(ApiErrorCode.USER_TOKEN_NOT_FOUND));

        String newAccessToken = authTokenProvider.createAuthToken(userId).getToken();
        String newRefreshToken = RandomStringUtils.randomAlphanumeric(REFRESH_TOKEN_LENGTH);

        userToken.reissue(newRefreshToken, REFRESH_TOKEN_MAX_AGE);

        return new LoginTokenResponseDto(newAccessToken, newRefreshToken);
    }

    @Transactional(readOnly = true)
    public LoginCheckResponseDto check(HttpServletRequest request) {
        String accessToken = AccessTokenExtractor.extract(request).orElse(null);
        if (accessToken == null) {
            return LoginCheckResponseDto.fail();
        }

        AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);
        if (!authToken.validate()) {
            return LoginCheckResponseDto.fail();
        }

        Long userId = authToken.getUserId();
        if (userId == null) {
            return LoginCheckResponseDto.fail();
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return LoginCheckResponseDto.fail();
        }

        return LoginCheckResponseDto.success(user);
    }
}
