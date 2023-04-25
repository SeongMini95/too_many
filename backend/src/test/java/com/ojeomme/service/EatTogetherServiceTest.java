package com.ojeomme.service;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherpost.repository.EatTogetherPostRepository;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherPostRequestDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EatTogetherServiceTest {

    @InjectMocks
    private EatTogetherService eatTogetherService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegionCodeRepository regionCodeRepository;

    @Mock
    private EatTogetherPostRepository eatTogetherPostRepository;

    @Mock
    private ImageService imageService;

    @Nested
    class writeEatTogetherPost {

        private final WriteEatTogetherPostRequestDto requestDto = WriteEatTogetherPostRequestDto.builder()
                .regionCode("1234")
                .subject("테스트 제목")
                .content("이미지1 <img src=\"http://localhost:4000/temp/image1.png\"> 이미지2 http://localhost:4000/temp/image2.png 본문 내용")
                .build();

        @Test
        void 게시판에_글을_작성한다() throws IOException {
            // given
            ReflectionTestUtils.setField(eatTogetherService, "host", "http://localhost:4000");

            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(mock(RegionCode.class)));
            given(imageService.copyImage(eq("http://localhost:4000/temp/image1.png"))).willReturn("http://localhost:4000/image1.png");
            given(eatTogetherPostRepository.save(any(EatTogetherPost.class))).willReturn(mock(EatTogetherPost.class));

            // when
            Long postId = eatTogetherService.writeEatTogetherPost(1L, requestDto);

            // then
            assertThat(postId).isNotNull();
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.writeEatTogetherPost(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 지역코드가_없으면_RegionCodeNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.writeEatTogetherPost(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }
    }
}