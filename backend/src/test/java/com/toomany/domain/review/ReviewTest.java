package com.toomany.domain.review;

import com.toomany.domain.reviewimage.ReviewImage;
import com.toomany.domain.reviewrecommend.ReviewRecommend;
import com.toomany.domain.reviewrecommend.enums.RecommendType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    @Nested
    class addImages {

        @Test
        void 이미지를_추가한다() {
            // given
            Review review = new Review();

            List<ReviewImage> reviewImages = List.of(
                    ReviewImage.builder().imageUrl("111").build(),
                    ReviewImage.builder().imageUrl("222").build()
            );

            // when
            review.addImages(reviewImages);

            // then
            assertThat(review.getReviewImages().size()).isEqualTo(reviewImages.size());
            for (int i = 0; i < review.getReviewImages().size(); i++) {
                assertThat(review.getReviewImages().get(i).getImageUrl()).isEqualTo(reviewImages.get(i).getImageUrl());
            }
        }
    }

    @Nested
    class addRecommends {

        @Test
        void 추천_포인트를_추가한다() {
            // given
            Review review = new Review();

            List<ReviewRecommend> reviewRecommends = List.of(
                    ReviewRecommend.builder().recommendType(RecommendType.TASTE).build(),
                    ReviewRecommend.builder().recommendType(RecommendType.VALUE_FOR_MONEY).build()
            );

            // when
            review.addRecommends(reviewRecommends);

            // then
            assertThat(review.getReviewRecommends().size()).isEqualTo(reviewRecommends.size());
            for (int i = 0; i < review.getReviewRecommends().size(); i++) {
                assertThat(review.getReviewRecommends().get(i).getRecommendType()).isEqualTo(reviewRecommends.get(i).getRecommendType());
            }
        }
    }
}