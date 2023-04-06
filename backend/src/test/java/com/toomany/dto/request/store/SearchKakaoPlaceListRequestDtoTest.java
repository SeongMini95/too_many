package com.toomany.dto.request.store;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchKakaoPlaceListRequestDtoTest {

    @Nested
    class setX {

        @Test
        void x를_설정한다() {
            // given
            SearchPlaceListRequestDto requestDto = new SearchPlaceListRequestDto();

            // when
            requestDto.setX("127");

            // then
            assertThat(requestDto.getX()).isEqualTo("127");
        }

        @Test
        void x를_설정한다_null() {
            // given
            SearchPlaceListRequestDto requestDto = new SearchPlaceListRequestDto();

            // when
            requestDto.setX(null);

            // then
            assertThat(requestDto.getX()).isEqualTo("");
        }
    }

    @Nested
    class setY {

        @Test
        void y를_설정한다() {
            // given
            SearchPlaceListRequestDto requestDto = new SearchPlaceListRequestDto();

            // when
            requestDto.setY("34");

            // then
            assertThat(requestDto.getY()).isEqualTo("34");
        }

        @Test
        void y를_설정한다_null() {
            // given
            SearchPlaceListRequestDto requestDto = new SearchPlaceListRequestDto();

            // when
            requestDto.setY(null);

            // then
            assertThat(requestDto.getY()).isEqualTo("");
        }
    }

    @Nested
    class setPage {

        @Test
        void page를_설정한다() {
            // given
            SearchPlaceListRequestDto requestDto = new SearchPlaceListRequestDto();

            // when
            requestDto.setPage(3);

            // then
            assertThat(requestDto.getPage()).isEqualTo(3);
        }

        @Test
        void page를_설정한다_null() {
            // given
            SearchPlaceListRequestDto requestDto = new SearchPlaceListRequestDto();

            // when
            requestDto.setPage(null);

            // then
            assertThat(requestDto.getPage()).isEqualTo(1);
        }

        @Test
        void page를_설정한다_negative() {
            // given
            SearchPlaceListRequestDto requestDto = new SearchPlaceListRequestDto();

            // when
            requestDto.setPage(-1);

            // then
            assertThat(requestDto.getPage()).isEqualTo(1);
        }
    }
}