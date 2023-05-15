package com.ojeomme.service;

import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.dto.response.reviewimage.PreviewImageListResponseDto;
import com.ojeomme.dto.response.reviewimage.ReviewImageListResponseDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReviewImageServiceTest {

    @InjectMocks
    private ReviewImageService reviewImageService;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Nested
    class getPreviewImageList {

        @Test
        void 리뷰_이미지_미리보기를_가져온다() {
            // given
            PreviewImageListResponseDto dto = new PreviewImageListResponseDto(100L, List.of(
                    "image1",
                    "image2",
                    "image3",
                    "image4",
                    "image5"
            ));
            given(reviewImageRepository.getPreviewImageList(anyLong())).willReturn(dto);

            // when
            PreviewImageListResponseDto responseDto = reviewImageService.getPreviewImageList(1L);

            // then
            assertThat(responseDto.getImageCnt()).isEqualTo(dto.getImageCnt());
            assertThat(responseDto.getImages()).isEqualTo(dto.getImages());
        }
    }

    @Nested
    class getReviewImageList {

        @Test
        void 리뷰_이미지를_가져온다() {
            // given
            ReviewImageListResponseDto images = new ReviewImageListResponseDto(true, 3L, List.of(
                    "http://localhost:4000/1.png",
                    "http://localhost:4000/2.png",
                    "http://localhost:4000/3.png"
            ));
            given(reviewImageRepository.getReviewImageList(anyLong(), anyLong())).willReturn(images);

            // when
            ReviewImageListResponseDto responseDto = reviewImageService.getReviewImageList(1L, 1L);

            // then
            assertThat(responseDto.getImages()).hasSameSizeAs(images.getImages());
            assertThat(responseDto.getImages()).isEqualTo(images.getImages());
        }
    }
}