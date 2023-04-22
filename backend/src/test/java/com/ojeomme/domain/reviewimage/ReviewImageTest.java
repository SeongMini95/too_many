package com.ojeomme.domain.reviewimage;

import com.ojeomme.domain.review.Review;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewImageTest {

    @Nested
    class testEquals {

        @Test
        void 오브젝트_본인과_비교한다() {
            // given
            ReviewImage reviewImage = ReviewImage.builder().build();

            // when
            boolean equals = reviewImage.equals(reviewImage);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 오브젝트가_null이다() {
            // given
            ReviewImage reviewImage = ReviewImage.builder().build();

            // when
            boolean equals = reviewImage.equals(null);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 다른_클래스이다() {
            // given
            ReviewImage reviewImage = ReviewImage.builder().build();
            Object object = new Object();

            // when
            boolean equals = reviewImage.equals(object);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디_이미지가_다르다() {
            // given
            ReviewImage reviewImage1 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            ReviewImage reviewImage2 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(2L)
                            .build())
                    .imageUrl("2")
                    .build();

            // when
            boolean equals = reviewImage1.equals(reviewImage2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디는_같은데_이미지는_다르다() {
            // given
            ReviewImage reviewImage1 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            ReviewImage reviewImage2 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .imageUrl("2")
                    .build();

            // when
            boolean equals = reviewImage1.equals(reviewImage2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디는_다르고_이미지는_같다() {
            // given
            ReviewImage reviewImage1 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            ReviewImage reviewImage2 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(2L)
                            .build())
                    .imageUrl("1")
                    .build();

            // when
            boolean equals = reviewImage1.equals(reviewImage2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디_이미지_모두_같다() {
            // given
            ReviewImage reviewImage1 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            ReviewImage reviewImage2 = ReviewImage.builder()
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();

            // when
            boolean equals = reviewImage1.equals(reviewImage2);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 리뷰_이미지의_아이디가_같다() {
            // given
            ReviewImage reviewImage1 = ReviewImage.builder()
                    .id(1L)
                    .build();
            ReviewImage reviewImage2 = ReviewImage.builder()
                    .id(1L)
                    .build();

            // when
            boolean equals = reviewImage1.equals(reviewImage2);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 리뷰_이미지의_아이디가_다르다() {
            // given
            ReviewImage reviewImage1 = ReviewImage.builder()
                    .id(1L)
                    .review(Review.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            ReviewImage reviewImage2 = ReviewImage.builder()
                    .id(2L)
                    .review(Review.builder()
                            .id(2L)
                            .build())
                    .imageUrl("2")
                    .build();

            // when
            boolean equals = reviewImage1.equals(reviewImage2);

            // then
            assertThat(equals).isFalse();
        }
    }

    @Nested
    class testHashCode {

        @Test
        void 해시코드를_가져온다() {
            // given
            ReviewImage reviewImage = ReviewImage.builder().build();

            // when
            int hashcode = reviewImage.hashCode();

            // then
            assertThat(hashcode).isNotZero();
        }
    }
}