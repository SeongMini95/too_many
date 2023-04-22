package com.ojeomme.domain.storelikelog;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StoreLikeLogIdTest {

    @Nested
    class testEquals {

        @Test
        void 같은_오브젝트다() {
            // given
            StoreLikeLogId id = StoreLikeLogId.builder().build();

            // when
            boolean equals = id.equals(id);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void null과_비교한다() {
            // given
            StoreLikeLogId id = StoreLikeLogId.builder().build();

            // when
            boolean equals = id.equals(null);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 같은_클래스가_아니다() {
            // given
            StoreLikeLogId id = StoreLikeLogId.builder().build();
            Object object = new Object();

            // when
            boolean equals = id.equals(object);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 매장과_유저_모두_다르다() {
            // given
            StoreLikeLogId id1 = StoreLikeLogId.builder()
                    .storeId(1L)
                    .userId(1L)
                    .build();
            StoreLikeLogId id2 = StoreLikeLogId.builder()
                    .storeId(2L)
                    .userId(2L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 매장은_같고_유저는_다르다() {
            // given
            StoreLikeLogId id1 = StoreLikeLogId.builder()
                    .storeId(1L)
                    .userId(1L)
                    .build();
            StoreLikeLogId id2 = StoreLikeLogId.builder()
                    .storeId(1L)
                    .userId(2L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 매장은_다르고_유저는_같다() {
            // given
            StoreLikeLogId id1 = StoreLikeLogId.builder()
                    .storeId(1L)
                    .userId(1L)
                    .build();
            StoreLikeLogId id2 = StoreLikeLogId.builder()
                    .storeId(2L)
                    .userId(1L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 매장과_유저_모두_같다() {
            // given
            StoreLikeLogId id1 = StoreLikeLogId.builder()
                    .storeId(1L)
                    .userId(1L)
                    .build();
            StoreLikeLogId id2 = StoreLikeLogId.builder()
                    .storeId(1L)
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
            StoreLikeLogId id = StoreLikeLogId.builder().build();

            // when
            int hashcode = id.hashCode();

            // then
            assertThat(hashcode).isNotZero();
        }
    }
}