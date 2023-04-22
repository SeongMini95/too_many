package com.ojeomme.domain.reviewlikelog;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewLikeLogIdTest {

    @Nested
    class testEquals {

        @Test
        void 오브젝트_본인과_비교한다() {
            // given
            ReviewLikeLogId id = ReviewLikeLogId.builder().build();

            // when
            boolean equals = id.equals(id);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 비교_오브젝트가_null이다() {
            // given
            ReviewLikeLogId id = ReviewLikeLogId.builder().build();

            // when
            boolean equals = id.equals(null);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 같은_클래스가_아니다() {
            // given
            ReviewLikeLogId id = ReviewLikeLogId.builder().build();
            Object object = new Object();

            // when
            boolean equals = id.equals(object);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰와_유저가_모두_다르다() {
            // given
            ReviewLikeLogId id1 = ReviewLikeLogId.builder()
                    .reviewId(1L)
                    .userId(1L)
                    .build();
            ReviewLikeLogId id2 = ReviewLikeLogId.builder()
                    .reviewId(2L)
                    .userId(2L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰는_같고_유저는_다르다() {
            // given
            ReviewLikeLogId id1 = ReviewLikeLogId.builder()
                    .reviewId(1L)
                    .userId(1L)
                    .build();
            ReviewLikeLogId id2 = ReviewLikeLogId.builder()
                    .reviewId(1L)
                    .userId(2L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰는_다르고_유저는_같다() {
            // given
            ReviewLikeLogId id1 = ReviewLikeLogId.builder()
                    .reviewId(1L)
                    .userId(1L)
                    .build();
            ReviewLikeLogId id2 = ReviewLikeLogId.builder()
                    .reviewId(2L)
                    .userId(1L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰_유저_모두_같다() {
            // given
            ReviewLikeLogId id1 = ReviewLikeLogId.builder()
                    .reviewId(1L)
                    .userId(1L)
                    .build();
            ReviewLikeLogId id2 = ReviewLikeLogId.builder()
                    .reviewId(1L)
                    .userId(1L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isTrue();
        }
    }

    @Nested
    class testHashCode {

        @Test
        void 해시코드를_가져온다() {
            // given
            ReviewLikeLogId id = ReviewLikeLogId.builder().build();

            // when
            int hashcode = id.hashCode();

            // then
            assertThat(hashcode).isNotZero();
        }
    }
}