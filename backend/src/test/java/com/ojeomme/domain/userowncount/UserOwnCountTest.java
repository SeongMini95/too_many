package com.ojeomme.domain.userowncount;

import com.ojeomme.domain.user.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserOwnCountTest {

    private final UserOwnCount userOwnCount = UserOwnCount.builder()
            .user(User.builder()
                    .id(1L)
                    .build())
            .reviewCnt(5)
            .likeCnt(5)
            .build();

    @Nested
    class increaseReview {

        @Test
        void 리뷰_카운트가_증가() {
            // given

            // when
            userOwnCount.increaseReview();

            // then
            assertThat(userOwnCount.getReviewCnt()).isEqualTo(6);
        }
    }

    @Nested
    class decreaseReview {

        @Test
        void 리뷰_카운트가_감소() {
            // given

            // when
            userOwnCount.decreaseReview();

            // then
            assertThat(userOwnCount.getReviewCnt()).isEqualTo(4);
        }
    }

    @Nested
    class increaseLike {

        @Test
        void 좋아요가_증가() {
            // given

            // when
            userOwnCount.increaseLike();

            // then
            assertThat(userOwnCount.getLikeCnt()).isEqualTo(6);
        }
    }

    @Nested
    class decreaseLike {

        @Test
        void 좋아요가_감소() {
            // given

            // when
            userOwnCount.decreaseLike(1);

            // when
            assertThat(userOwnCount.getLikeCnt()).isEqualTo(4);
        }
    }
}