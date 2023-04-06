package com.toomany.service;

import com.toomany.common.maps.client.KakaoAddressClient;
import com.toomany.common.maps.client.KakaoRegionCodeClient;
import com.toomany.common.maps.entity.KakaoAddressCoord;
import com.toomany.common.maps.entity.KakaoRegionCode;
import com.toomany.domain.regioncode.RegionCode;
import com.toomany.domain.regioncode.repository.RegionCodeRepository;
import com.toomany.dto.response.region.RegionCoordResponseDto;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @InjectMocks
    private RegionService regionService;

    @Mock
    private RegionCodeRepository regionCodeRepository;

    @Mock
    private KakaoRegionCodeClient kakaoRegionCodeClient;

    @Mock
    private KakaoAddressClient kakaoAddressClient;

    @Nested
    class getRegionCodeOfCoord {

        @Test
        void 좌표의_지역_코드를_가져온다_depth4() {
            // given
            KakaoRegionCode kakaoRegionCode = mock(KakaoRegionCode.class);
            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(kakaoRegionCode.getCode()).willReturn("2671025321");

            RegionCode regionCode = RegionCode.builder()
                    .code("2671025321")
                    .upCode(RegionCode.builder()
                            .code("2671025300")
                            .build())
                    .regionDepth(4)
                    .build();
            given(regionCodeRepository.findById(eq("2671025321"))).willReturn(Optional.of(regionCode));

            // when
            String code = regionService.getRegionCodeOfCoord("127", "34");

            // then
            assertThat(code).isEqualTo("2671025300");
        }

        @Test
        void 좌표의_지역_코드를_가져온다_depth4x() {
            // given
            KakaoRegionCode kakaoRegionCode = mock(KakaoRegionCode.class);
            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(kakaoRegionCode.getCode()).willReturn("2671025321");

            RegionCode regionCode = RegionCode.builder()
                    .code("2671025321")
                    .upCode(RegionCode.builder()
                            .code("2671025300")
                            .build())
                    .regionDepth(3)
                    .build();
            given(regionCodeRepository.findById(eq("2671025321"))).willReturn(Optional.of(regionCode));

            // when
            String code = regionService.getRegionCodeOfCoord("127", "34");

            // then
            assertThat(code).isEqualTo("2671025321");
        }

        @Test
        void region_code가_없으면_RegionCodeNotFound를_발생한다() {
            // given
            KakaoRegionCode kakaoRegionCode = mock(KakaoRegionCode.class);
            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(kakaoRegionCode.getCode()).willReturn("2671025321");

            given(regionCodeRepository.findById(eq("2671025321"))).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> regionService.getRegionCodeOfCoord("127", "34"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }
    }

    @Nested
    class getRegionCoord {

        @Test
        void 지역의_좌표를_가져온다() {
            // given
            RegionCode regionCode = RegionCode.builder()
                    .code("1111010100")
                    .upCode(RegionCode.builder()
                            .code("1111000000")
                            .upCode(RegionCode.builder()
                                    .code("1100000000")
                                    .regionDepth(1)
                                    .regionName("서울특별시")
                                    .build())
                            .regionDepth(2)
                            .regionName("종로구")
                            .build())
                    .regionDepth(3)
                    .regionName("청운동")
                    .build();
            given(regionCodeRepository.findById(eq("1111010100"))).willReturn(Optional.of(regionCode));
            given(regionCodeRepository.findById(eq("1111000000"))).willReturn(Optional.of(regionCode.getUpCode()));
            given(regionCodeRepository.findById("1100000000")).willReturn(Optional.of(regionCode.getUpCode().getUpCode()));

            KakaoAddressCoord kakaoAddressCoord = KakaoAddressCoord.builder()
                    .meta(KakaoAddressCoord.Meta.builder()
                            .totalCount(1)
                            .build())
                    .documents(List.of(
                            KakaoAddressCoord.Document.builder()
                                    .x("127")
                                    .y("34")
                                    .build()
                    ))
                    .build();
            given(kakaoAddressClient.getKakaoAddressCoord(eq("서울특별시 종로구 청운동"))).willReturn(kakaoAddressCoord);

            // when
            RegionCoordResponseDto responseDto = regionService.getRegionCoord("1111010100");

            // then
            assertThat(responseDto.getX()).isEqualTo("127");
            assertThat(responseDto.getY()).isEqualTo("34");
        }

        @Test
        void 초기_지역_코드가_존재하지_않으면_RegionCodeNotFoundException를_발생한다() {
            // given
            given(regionCodeRepository.findById(eq("1111010100"))).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> regionService.getRegionCoord("1111010100"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }

        @Test
        void 상위_지역_코드가_존재하지_않으면_RegionCodeNotFoundException를_발생한다() {
            // given
            RegionCode regionCode = RegionCode.builder()
                    .code("1111010100")
                    .upCode(RegionCode.builder()
                            .code("1111000000")
                            .upCode(RegionCode.builder()
                                    .code("1100000000")
                                    .regionDepth(1)
                                    .regionName("서울특별시")
                                    .build())
                            .regionDepth(2)
                            .regionName("종로구")
                            .build())
                    .regionDepth(3)
                    .regionName("청운동")
                    .build();
            given(regionCodeRepository.findById(eq("1111010100"))).willReturn(Optional.of(regionCode));
            given(regionCodeRepository.findById(eq("1111000000"))).willReturn(Optional.of(regionCode.getUpCode()));
            given(regionCodeRepository.findById(eq("1100000000"))).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> regionService.getRegionCoord("1111010100"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }
    }
}