package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoAddressClient;
import com.ojeomme.common.maps.client.KakaoRegionCodeClient;
import com.ojeomme.common.maps.entity.KakaoAddressCoord;
import com.ojeomme.common.maps.entity.KakaoRegionCode;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.response.region.CoordOfRegionResponseDto;
import com.ojeomme.dto.response.region.RegionCodeListResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
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
    class getCoordOfRegionCode {

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
            given(regionCodeRepository.findById(eq("1100000000"))).willReturn(Optional.of(regionCode.getUpCode().getUpCode()));

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
            CoordOfRegionResponseDto responseDto = regionService.getCoordOfRegionCode("1111010100");

            // then
            assertThat(responseDto.getX()).isEqualTo("127");
            assertThat(responseDto.getY()).isEqualTo("34");
        }

        @Test
        void 지역의_좌표를_가져온다_만약에_depth가_4면_상위_코드로() {
            // given
            RegionCode regionCode = RegionCode.builder()
                    .code("2671025021")
                    .upCode(RegionCode.builder()
                            .code("2671025000")
                            .upCode(RegionCode.builder()
                                    .code("2671000000")
                                    .upCode(RegionCode.builder()
                                            .code("2600000000")
                                            .regionDepth(1)
                                            .regionName("부산")
                                            .build())
                                    .regionDepth(2)
                                    .regionName("기장군")
                                    .build())
                            .regionDepth(3)
                            .regionName("기장읍")
                            .build())
                    .regionDepth(4)
                    .regionName("동부리")
                    .build();
            given(regionCodeRepository.findById(eq("2671025021"))).willReturn(Optional.of(regionCode));
            given(regionCodeRepository.findById(eq("2671000000"))).willReturn(Optional.of(regionCode.getUpCode().getUpCode()));
            given(regionCodeRepository.findById(eq("2600000000"))).willReturn(Optional.of(regionCode.getUpCode().getUpCode().getUpCode()));

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
            given(kakaoAddressClient.getKakaoAddressCoord(eq("부산 기장군 기장읍"))).willReturn(kakaoAddressCoord);

            // when
            CoordOfRegionResponseDto responseDto = regionService.getCoordOfRegionCode("2671025021");

            // then
            assertThat(responseDto.getX()).isEqualTo("127");
            assertThat(responseDto.getY()).isEqualTo("34");
        }

        @Test
        void 초기_지역_코드가_존재하지_않으면_RegionCodeNotFoundException를_발생한다() {
            // given
            given(regionCodeRepository.findById(eq("1111010100"))).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> regionService.getCoordOfRegionCode("1111010100"));

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
            ApiException exception = assertThrows(ApiException.class, () -> regionService.getCoordOfRegionCode("1111010100"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }
    }

    @Nested
    class getRegionCodeList {

        @Test
        void 지역코드의_이름과_코드를_가져온다() {
            // given
            RegionCode regionCode1_1 = RegionCode.builder().code("1_1").regionDepth(1).regionName("1_1").build();
            RegionCode regionCode1_2 = RegionCode.builder().code("1_2").regionDepth(1).regionName("1_2").build();
            RegionCode regionCode2_1 = RegionCode.builder().code("2_1").upCode(regionCode1_1).regionDepth(2).regionName("2_1").build();
            RegionCode regionCode2_2 = RegionCode.builder().code("2_2").upCode(regionCode1_1).regionDepth(2).regionName("2_2").build();
            RegionCode regionCode2_3 = RegionCode.builder().code("2_3").upCode(regionCode1_2).regionDepth(2).regionName("2_3").build();
            RegionCode regionCode3_1 = RegionCode.builder().code("3_1").upCode(regionCode2_1).regionDepth(3).regionName("3_1").build();
            RegionCode regionCode3_2 = RegionCode.builder().code("3_2").upCode(regionCode2_2).regionDepth(3).regionName("3_2").build();
            RegionCode regionCode3_3 = RegionCode.builder().code("3_3").upCode(regionCode2_3).regionDepth(3).regionName("3_3").build();
            List<RegionCode> regionCodes = List.of(regionCode1_1, regionCode1_2, regionCode2_1, regionCode2_2, regionCode2_3, regionCode3_1, regionCode3_2, regionCode3_3);
            given(regionCodeRepository.findAllByRegionDepthNot(eq(4))).willReturn(regionCodes);

            // when
            RegionCodeListResponseDto responseDto = regionService.getRegionCodeList();

            List<RegionCodeListResponseDto.RegionCodeResponseDto> regions = responseDto.getRegions();

            // then
            assertThat(regions.get(0).getCode()).isEqualTo("1_1");
            assertThat(regions.get(1).getCode()).isEqualTo("1_2");

            assertThat(regions.get(0).getChildren().get(0).getCode()).isEqualTo("2_1");
            assertThat(regions.get(0).getChildren().get(1).getCode()).isEqualTo("2_2");
            assertThat(regions.get(1).getChildren().get(0).getCode()).isEqualTo("2_3");

            assertThat(regions.get(0).getChildren().get(0).getChildren().get(0).getCode()).isEqualTo("3_1");
            assertThat(regions.get(0).getChildren().get(1).getChildren().get(0).getCode()).isEqualTo("3_2");
            assertThat(regions.get(1).getChildren().get(0).getChildren().get(0).getCode()).isEqualTo("3_3");
        }
    }
}