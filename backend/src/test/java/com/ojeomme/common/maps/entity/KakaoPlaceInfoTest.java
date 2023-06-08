package com.ojeomme.common.maps.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoPlaceInfoTest {

    @Nested
    class getIsExist {

        @Test
        void isExist를_가져온다() {
            // given
            KakaoPlaceInfo kakaoPlaceInfo = KakaoPlaceInfo.builder()
                    .isExist(true)
                    .build();

            // when
            boolean isExist = kakaoPlaceInfo.getIsExist();

            // then
            assertThat(isExist).isTrue();
        }
    }

    @Nested
    class getPlaceId {

        @Test
        void placeId를_가져온다() {
            // given
            KakaoPlaceInfo kakaoPlaceInfo = KakaoPlaceInfo.builder()
                    .basicInfo(KakaoPlaceInfo.BasicInfo.builder()
                            .cid(1234L)
                            .build())
                    .build();

            // when
            Long placeId = kakaoPlaceInfo.getPlaceId();

            // then
            assertThat(placeId).isEqualTo(1234);
        }
    }

    @Nested
    class getPlaceName {

        @Test
        void 매장명을_가져온다() {
            // given
            KakaoPlaceInfo kakaoPlaceInfo = KakaoPlaceInfo.builder()
                    .basicInfo(KakaoPlaceInfo.BasicInfo.builder()
                            .placenamefull("스시코우지")
                            .build())
                    .build();

            // when
            String placeName = kakaoPlaceInfo.getPlaceName();

            // then
            assertThat(placeName).isEqualTo("스시코우지");
        }
    }

    @Nested
    class getRoadAddress {

        @Test
        void 도로명_주소를_가져온다() {
            // given
            KakaoPlaceInfo kakaoPlaceInfo = KakaoPlaceInfo.builder()
                    .basicInfo(KakaoPlaceInfo.BasicInfo.builder()
                            .address(KakaoPlaceInfo.BasicInfo.Address.builder()
                                    .addrdetail("어넥스 B동 3층")
                                    .newaddr(KakaoPlaceInfo.BasicInfo.Address.Newaddr.builder()
                                            .newaddrfull("도산대로 318")
                                            .build())
                                    .region(KakaoPlaceInfo.BasicInfo.Address.Region.builder()
                                            .newaddrfullname("서울시 강남구")
                                            .build())
                                    .build())
                            .build())
                    .build();

            // when
            String roadAddress = kakaoPlaceInfo.getRoadAddress();

            // then
            assertThat(roadAddress).isEqualTo("서울시 강남구 도산대로 318 어넥스 B동 3층");
        }

        @Test
        void 상세주소가_없다() {
            // given
            KakaoPlaceInfo kakaoPlaceInfo = KakaoPlaceInfo.builder()
                    .basicInfo(KakaoPlaceInfo.BasicInfo.builder()
                            .address(KakaoPlaceInfo.BasicInfo.Address.builder()
                                    .newaddr(KakaoPlaceInfo.BasicInfo.Address.Newaddr.builder()
                                            .newaddrfull("도산대로 318")
                                            .build())
                                    .region(KakaoPlaceInfo.BasicInfo.Address.Region.builder()
                                            .newaddrfullname("서울시 강남구")
                                            .build())
                                    .build())
                            .build())
                    .build();

            // when
            String roadAddress = kakaoPlaceInfo.getRoadAddress();

            // then
            assertThat(roadAddress).isEqualTo("서울시 강남구 도산대로 318");
        }
    }

    @Nested
    class getAddress {

        @Test
        void 구_주소를_가져온다() {
            // given
            KakaoPlaceInfo kakaoPlaceInfo = KakaoPlaceInfo.builder()
                    .basicInfo(KakaoPlaceInfo.BasicInfo.builder()
                            .address(KakaoPlaceInfo.BasicInfo.Address.builder()
                                    .addrbunho("92")
                                    .region(KakaoPlaceInfo.BasicInfo.Address.Region.builder()
                                            .fullname("서울시 강남구 논현동")
                                            .build())
                                    .build())
                            .build())
                    .build();

            // when
            String address = kakaoPlaceInfo.getAddress();

            // then
            assertThat(address).isEqualTo("서울시 강남구 논현동 92");
        }
    }
}