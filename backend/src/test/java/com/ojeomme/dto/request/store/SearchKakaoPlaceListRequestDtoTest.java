package com.ojeomme.dto.request.store;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchKakaoPlaceListRequestDtoTest {

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