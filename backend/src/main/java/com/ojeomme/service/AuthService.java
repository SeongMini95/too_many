package com.ojeomme.service;

import com.ojeomme.common.jwt.entity.AuthToken;
import com.ojeomme.common.jwt.handler.AccessTokenExtractor;
import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.common.oauth.handler.OauthClientHandler;
import com.ojeomme.common.oauth.info.OauthUserInfo;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.domain.userowncount.UserOwnCount;
import com.ojeomme.domain.userowncount.repository.UserOwnCountRepository;
import com.ojeomme.domain.usertoken.UserToken;
import com.ojeomme.domain.usertoken.repository.UserTokenRepository;
import com.ojeomme.dto.request.auth.ReissueTokenRequestDto;
import com.ojeomme.dto.response.auth.LoginCheckResponseDto;
import com.ojeomme.dto.response.auth.LoginTokenResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import com.ojeomme.exception.auth.AuthTokenBeforeExpiredException;
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
    private final UserOwnCountRepository userOwnCountRepository;

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
            userOwnCountRepository.save(UserOwnCount.builder()
                    .user(user)
                    .reviewCnt(0)
                    .likeCnt(0)
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
