package com.toomany.common.maps.entity;

import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KakaoRegionCodeTest {

    @Nested
    class getCode {

        @Test
        void 지역코드를_가져온다() {
            // given
            KakaoRegionCode kakaoRegionCode = KakaoRegionCode.builder()
                    .documents(List.of(
                            KakaoRegionCode.Document.builder().regionType("H").code("111").build(),
                            KakaoRegionCode.Document.builder().regionType("B").code("222").build()
                    ))
                    .build();

            // when
            String code = kakaoRegionCode.getCode();

            // then
            assertThat(code).isEqualTo("222");
        }

        @Test
        void document가_없으면_NotExistPlaceException를_발생한다() {
            // given
            KakaoRegionCode kakaoRegionCode = KakaoRegionCode.builder().documents(Collections.emptyList()).build();

            // then
            ApiException exception = assertThrows(ApiException.class, kakaoRegionCode::getCode);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.NOT_EXIST_PLACE);
        }
    }
}