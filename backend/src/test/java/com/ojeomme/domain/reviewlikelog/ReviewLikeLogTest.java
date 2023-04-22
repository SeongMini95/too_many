package com.ojeomme.domain.reviewlikelog;

import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.user.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewLikeLogTest {

    @Nested
    class isNew {

        @Test
        void 새로운_entity() {
            // given
            ReviewLikeLog reviewLikeLog = ReviewLikeLog.builder()
                    .review(Review.builder().id(1L).build())
                    .user(User.builder().id(1L).build())
                    .build();

            // when
            boolean isNew = reviewLikeLog.isNew();

            // then
            assertThat(isNew).isTrue();
        }

        @Test
        void 이미_존재하는_entity() {
            // given
            ReviewLikeLog reviewLikeLog = ReviewLikeLog.builder()
                    .review(Review.builder().id(1L).build())
                    .user(User.builder().id(1L).build())
                    .build();
            reviewLikeLog.setDateTime(LocalDateTime.now(), LocalDateTime.now());

            // when
            boolean isNew = reviewLikeLog.isNew();

            // then
            assertThat(isNew).isFalse();
        }
    }
}