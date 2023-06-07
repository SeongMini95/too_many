package com.ojeomme.service;

import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.user.ModifyMyInfoRequestDto;
import com.ojeomme.dto.response.user.ModifyMyInfoResponseDto;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Nested
    class modifyMyInfo {

        @Test
        void 내_정보를_수정한다() throws IOException {
            // given
            User user = User.builder()
                    .oauthProvider(OauthProvider.NAVER)
                    .email("test123@naver.com")
                    .profile("http://localhost:4000/profile.png")
                    .nickname("테스트")
                    .build();
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            ModifyMyInfoRequestDto requestDto = ModifyMyInfoRequestDto.builder()
                    .nickname("수정")
                    .profile("http://lolcahost:4000/change.png")
                    .build();

            given(imageService.copyImage(anyString())).willReturn(requestDto.getProfile());

            // when
            ModifyMyInfoResponseDto responseDto = userService.modifyMyInfo(1L, requestDto);

            // then
            assertThat(responseDto.getNickname()).isEqualTo(requestDto.getNickname());
            assertThat(responseDto.getProfile()).isEqualTo(requestDto.getProfile());
        }

        @Test
        void 기본_프로필로_변경한다() throws IOException {
            // given
            User user = User.builder()
                    .oauthProvider(OauthProvider.NAVER)
                    .email("test123@naver.com")
                    .profile("http://localhost:4000/profile.png")
                    .nickname("테스트")
                    .build();
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            ModifyMyInfoRequestDto requestDto = ModifyMyInfoRequestDto.builder()
                    .nickname("수정")
                    .profile("")
                    .build();

            // when
            ModifyMyInfoResponseDto responseDto = userService.modifyMyInfo(1L, requestDto);

            // then
            assertThat(responseDto.getNickname()).isEqualTo(requestDto.getNickname());
            assertThat(responseDto.getProfile()).isBlank();
        }

        @Test
        void 내_정보를_수정하는데_유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            ModifyMyInfoRequestDto requestDto = ModifyMyInfoRequestDto.builder()
                    .nickname("수정")
                    .profile("")
                    .build();

            // when
            ApiException exception = assertThrows(ApiException.class, () -> userService.modifyMyInfo(1L, requestDto));

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