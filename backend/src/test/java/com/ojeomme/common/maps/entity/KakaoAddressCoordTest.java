package com.ojeomme.common.maps.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoAddressCoordTest {

    @Nested
    class exist {

        @Test
        void 존재한다() {
            // given
            KakaoAddressCoord kakaoAddressCoord = KakaoAddressCoord.builder()
                    .meta(KakaoAddressCoord.Meta.builder()
                            .totalCount(1)
                            .build())
                    .build();

            // when
            boolean exist = kakaoAddressCoord.exist();

            // then
            assertThat(exist).isTrue();
        }

        @Test
        void 존재하지_않는다() {
            // given
            KakaoAddressCoord kakaoAddressCoord = KakaoAddressCoord.builder()
                    .meta(KakaoAddressCoord.Meta.builder()
                            .totalCount(0)
                            .build())
                    .build();

            // when
            boolean exist = kakaoAddressCoord.exist();

            assertThat(exist).isFalse();
        }
    }

    @Nested
    class getX {

        @Test
        void x좌표를_가져온다() {
            // given
            KakaoAddressCoord kakaoAddressCoord = KakaoAddressCoord.builder()
                    .documents(List.of(
                            KakaoAddressCoord.Document.builder()
                                    .x("127")
                                    .build()
                    ))
                    .build();

            // when
            String x = kakaoAddressCoord.getX();

            // then
            assertThat(x).isEqualTo("127");
        }
    }

    @Nested
    class getY {

        @Test
        void y좌표를_가져온다() {
            // given
            KakaoAddressCoord kakaoAddressCoord = KakaoAddressCoord.builder()
                    .documents(List.of(
                            KakaoAddressCoord.Document.builder()
                                    .y("37")
                                    .build()
                    ))
                    .build();

            // when
            String y = kakaoAddressCoord.getY();

            // then
            assertThat(y).isEqualTo("37");
        }
    }
}