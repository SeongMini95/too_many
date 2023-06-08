package com.ojeomme.domain.reviewrecommend;

import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewRecommendTest {

    @Nested
    class testEquals {

        @Test
        void 오브젝트_본인과_비교한다() {
            // given
            ReviewRecommend reviewRecommend = ReviewRecommend.builder().build();

            // when
            boolean equals = reviewRecommend.equals(reviewRecommend);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 오브젝트가_null이다() {
            // given
            ReviewRecommend reviewRecommend = ReviewRecommend.builder().build();

            // when
            boolean equals = reviewRecommend.equals(null);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 다른_클래스이다() {
            // given
            ReviewRecommend reviewRecommend = ReviewRecommend.builder().build();
            Object object = new Object();

            // when
            boolean equals = reviewRecommend.equals(object);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디_타입이_다르다() {
            // given
            ReviewRecommend reviewRecommend1 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .recommendType(RecommendType.TASTE)
                    .build();
            ReviewRecommend reviewRecommend2 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(2L)
                            .build())
                    .recommendType(RecommendType.VALUE_FOR_MONEY)
                    .build();

            // when
            boolean equals = reviewRecommend1.equals(reviewRecommend2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디는_같은데_타입은_다르다() {
            // given
            ReviewRecommend reviewRecommend1 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .recommendType(RecommendType.TASTE)
                    .build();
            ReviewRecommend reviewRecommend2 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .recommendType(RecommendType.VALUE_FOR_MONEY)
                    .build();

            // when
            boolean equals = reviewRecommend1.equals(reviewRecommend2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디는_다르고_타입은_같다() {
            // given
            ReviewRecommend reviewRecommend1 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .recommendType(RecommendType.TASTE)
                    .build();
            ReviewRecommend reviewRecommend2 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(2L)
                            .build())
                    .recommendType(RecommendType.TASTE)
                    .build();

            // when
            boolean equals = reviewRecommend1.equals(reviewRecommend2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디_타입_모두_같다() {
            // given
            ReviewRecommend reviewRecommend1 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .recommendType(RecommendType.TASTE)
                    .build();
            ReviewRecommend reviewRecommend2 = ReviewRecommend.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .recommendType(RecommendType.TASTE)
                    .build();

            // when
            boolean equals = reviewRecommend1.equals(reviewRecommend2);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 리뷰_추천_포인트의_아이디가_같다() {
            // given
            ReviewRecommend reviewRecommend1 = ReviewRecommend.builder()
                    .id(1L)
                    .build();
            ReviewRecommend reviewRecommend2 = ReviewRecommend.builder()
                    .id(1L)
                    .build();

            // when
            boolean equals = reviewRecommend1.equals(reviewRecommend2);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 리뷰_추천_포인트의_아이디가_다르다() {
            // given
            ReviewRecommend reviewRecommend1 = ReviewRecommend.builder()
                    .id(1L)
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .recommendType(RecommendType.TASTE)
                    .build();
            ReviewRecommend reviewRecommend2 = ReviewRecommend.builder()
                    .id(2L)
                    .review(Review.builder()
                            .id(2L)
                            .build())
                    .recommendType(RecommendType.VALUE_FOR_MONEY)
                    .build();

            // when
            boolean equals = reviewRecommend1.equals(reviewRecommend2);

            // then
            assertThat(equals).isFalse();
        }
    }

    @Nested
    class testHashCode {

        @Test
        void 해시코드를_가져온다() {
            // given
            ReviewRecommend reviewRecommend = ReviewRecommend.builder().build();

            // when
            int hashcode = reviewRecommend.hashCode();

            // then
            assertThat(hashcode).isNotZero();
        }
    }
}