package com.ojeomme.service;

import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.user.ModifyNicknameRequestDto;
import com.ojeomme.dto.request.user.ModifyProfileRequestDto;
import com.ojeomme.dto.response.user.MyInfoResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Nested
    class modifyNickname {

        private final ModifyNicknameRequestDto requestDto = ModifyNicknameRequestDto.builder()
                .nickname("change123")
                .build();

        @Test
        void 닉네임을_변경한다() {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            userService.modifyNickname(1L, requestDto);

            // then
            then(user).should(times(1)).modifyNickname(eq(requestDto.getNickname()));
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> userService.modifyNickname(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    class modifyProfile {

        private final ModifyProfileRequestDto requestDto = ModifyProfileRequestDto.builder()
                .profile("http://localhost:4000/temp/change.png")
                .build();

        @Test
        void 프로필을_변경한다() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(imageService.copyImage(eq(requestDto.getProfile()))).willReturn("http://localhost:4000/change.png");

            // when
            String url = userService.modifyProfile(1L, requestDto);

            // then
            assertThat(url).isEqualTo("http://localhost:4000/change.png");
        }

        @Test
        void 기본_프로필로_변경한다() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            ModifyProfileRequestDto requestDto = ModifyProfileRequestDto.builder()
                    .profile("")
                    .build();

            // when
            String url = userService.modifyProfile(1L, requestDto);

            // then
            assertThat(url).isBlank();
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> userService.modifyProfile(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    class getMyInfo {

        @Test
        void 내_정보를_가져온다() {
            // given
            User user = User.builder()
                    .oauthProvider(OauthProvider.NAVER)
                    .email("test123@naver.com")
                    .profile("http://localhost:4000/profile.png")
                    .nickname("테스트")
                    .build();
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            MyInfoResponseDto responseDto = userService.getMyInfo(1L);

            // then
            assertThat(responseDto.getProvider()).isEqualTo(user.getOauthProvider().getCode());
            assertThat(responseDto.getEmail()).isEqualTo(user.getEmail());
            assertThat(responseDto.getProfile()).isEqualTo(user.getProfile());
            assertThat(responseDto.getNickname()).isEqualTo(user.getNickname());
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> userService.getMyInfo(1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }
    }
}