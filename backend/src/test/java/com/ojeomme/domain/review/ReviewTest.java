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
    class addImages {

        @Test
        void 이미지를_추가한다() {
            // given
            Review review = new Review();

            Set<ReviewImage> reviewImages = Set.of(
                    ReviewImage.builder().imageUrl("111").build(),
                    ReviewImage.builder().imageUrl("222").build()
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
            Review review = new Review();

            Set<ReviewRecommend> reviewRecommends = Set.of(
                    ReviewRecommend.builder().recommendType(RecommendType.TASTE).build(),
                    ReviewRecommend.builder().recommendType(RecommendType.VALUE_FOR_MONEY).build()
            );

            // when
            review.addRecommends(reviewRecommends);

            // then
            assertThat(review.getReviewRecommends()).isEqualTo(reviewRecommends);
        }
    }
}