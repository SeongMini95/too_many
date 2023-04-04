package com.toomany.dto.request.store;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchStoreRequestDtoTest {

    @Nested
    class setX {

        @Test
        void x를_설정한다() {
            // given
            SearchStoreRequestDto requestDto = new SearchStoreRequestDto();

            // when
            requestDto.setX("127");

            // then
            assertThat(requestDto.getX()).isEqualTo("127");
        }

        @Test
        void x를_설정한다_null() {
            // given
            SearchStoreRequestDto requestDto = new SearchStoreRequestDto();

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
            SearchStoreRequestDto requestDto = new SearchStoreRequestDto();

            // when
            requestDto.setY("34");

            // then
            assertThat(requestDto.getY()).isEqualTo("34");
        }

        @Test
        void y를_설정한다_null() {
            // given
            SearchStoreRequestDto requestDto = new SearchStoreRequestDto();

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
            SearchStoreRequestDto requestDto = new SearchStoreRequestDto();

            // when
            requestDto.setPage(3);

            // then
            assertThat(requestDto.getPage()).isEqualTo(3);
        }

        @Test
        void page를_설정한다_null() {
            // given
            SearchStoreRequestDto requestDto = new SearchStoreRequestDto();

            // when
            requestDto.setPage(null);

            // then
            assertThat(requestDto.getPage()).isEqualTo(1);
        }

        @Test
        void page를_설정한다_negative() {
            // given
            SearchStoreRequestDto requestDto = new SearchStoreRequestDto();

            // when
            requestDto.setPage(-1);

            // then
            assertThat(requestDto.getPage()).isEqualTo(1);
        }
    }
}