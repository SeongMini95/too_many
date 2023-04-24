package com.ojeomme.service;

import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.response.user.MyInfoResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

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