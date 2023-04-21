package com.ojeomme.domain.review;

import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    @Nested
    class modifyReview {

        @Test
        void 리뷰를_수정한다() {
            // given
            Review review = Review.builder()
                    .id(1L)
                    .build();

            Review modifyReview = Review.builder()
                    .starScore(3)
                    .content("테스트")
                    .revisitYn(true)
                    .build();

            Set<ReviewImage> reviewImages = Set.of(
                    ReviewImage.builder().review(review).imageUrl("111").build(),
                    ReviewImage.builder().review(review).imageUrl("222").build()
            );
            Set<ReviewRecommend> reviewRecommends = Set.of(
                    ReviewRecommend.builder().review(review).recommendType(RecommendType.TASTE).build(),
                    ReviewRecommend.builder().review(review).recommendType(RecommendType.VALUE_FOR_MONEY).build()
            );
            modifyReview.addImages(reviewImages);
            modifyReview.addRecommends(reviewRecommends);

            // when
            review.modifyReview(modifyReview);

            // then
            assertThat(review.getStarScore()).isEqualTo(modifyReview.getStarScore());
            assertThat(review.getContent()).isEqualTo(modifyReview.getContent());
            assertThat(review.isRevisitYn()).isEqualTo(modifyReview.isRevisitYn());
            assertThat(review.getReviewImages()).isEqualTo(modifyReview.getReviewImages());
            assertThat(review.getReviewRecommends()).isEqualTo(modifyReview.getReviewRecommends());
        }
    }

    @Nested
    class addImages {

        @Test
        void 이미지를_추가한다() {
            // given
            Review review = Review.builder()
                    .id(1L)
                    .build();

            Set<ReviewImage> reviewImages = Set.of(
                    ReviewImage.builder().review(review).imageUrl("111").build(),
                    ReviewImage.builder().review(review).imageUrl("222").build()
            );

            // when
            review.addImages(reviewImages);

            // then
            assertThat(review.getReviewImages()).isEqualTo(reviewImages);
        }
    }

    @Nested
    class addRecommends {

        @Test
        void 추천_포인트를_추가한다() {
            // given
            Review review = Review.builder()
                    .id(1L)
                    .build();

            Set<ReviewRecommend> reviewRecommends = Set.of(
                    ReviewRecommend.builder().review(review).recommendType(RecommendType.TASTE).build(),
                    ReviewRecommend.builder().review(review).recommendType(RecommendType.VALUE_FOR_MONEY).build()
            );

            // when
            review.addRecommends(reviewRecommends);

            // then
            assertThat(review.getReviewRecommends()).isEqualTo(reviewRecommends);
        }
    }
}