package com.ojeomme.domain.storereviewstatistics;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class StoreReviewStatisticsIdTest {

    @Nested
    class testEquals {

        @Test
        void 같은_오브젝트다() {
            // given
            StoreReviewStatisticsId id = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();

            // when
            boolean equals = id.equals(id);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 오브젝트가_null이다() {
            // given
            StoreReviewStatisticsId id = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();

            // when
            boolean equals = id.equals(null);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 서로_다른_클래스이다() {
            // given
            StoreReviewStatisticsId id = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();
            Object obj = new Object();

            // when
            boolean equals = id.equals(obj);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 생성일과_매장이_모두_다르다() {
            // given
            StoreReviewStatisticsId id1 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();
            StoreReviewStatisticsId id2 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now().minusDays(1))
                    .storeId(2L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 생성일은_같고_매장은_다르다() {
            // given
            StoreReviewStatisticsId id1 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();
            StoreReviewStatisticsId id2 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(2L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 생성일은_다르고_매장은_같다() {
            // given
            StoreReviewStatisticsId id1 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();
            StoreReviewStatisticsId id2 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now().minusDays(1))
                    .storeId(1L)
                    .build();

            // when
            boolean equals = id1.equals(id2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 생성일_매장_모드_같다() {
            // given
            StoreReviewStatisticsId id1 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();
            StoreReviewStatisticsId id2 = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
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
            StoreReviewStatisticsId id = StoreReviewStatisticsId.builder()
                    .statisticsDate(LocalDate.now())
                    .storeId(1L)
                    .build();

            // when
            int hashcode = id.hashCode();

            // then
            assertThat(hashcode).isNotZero();
        }
    }
}