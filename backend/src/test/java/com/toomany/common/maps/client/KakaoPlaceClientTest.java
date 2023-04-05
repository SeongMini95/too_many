package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.PlaceInfo;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KakaoPlaceClientTest {

    private MockWebServer mockWebServer;

    private static final String RESPONSE = "{\n" +
            "    \"isExist\": true,\n" +
            "    \"basicInfo\": {\n" +
            "        \"cid\": 23829251,\n" +
            "        \"placenamefull\": \"스시코우지\",\n" +
            "        \"mainphotourl\": \"http://t1.daumcdn.net/place/00B8FDFFB77F4691A3FC155507B8F2D7\",\n" +
            "        \"phonenum\": \"02-541-6200\",\n" +
            "        \"address\": {\n" +
            "            \"newaddr\": {\n" +
            "                \"newaddrfull\": \"도산대로 318\",\n" +
            "                \"bsizonno\": \"06054\"\n" +
            "            },\n" +
            "            \"region\": {\n" +
            "                \"name3\": \"논현동\",\n" +
            "                \"fullname\": \"서울 강남구 논현동\",\n" +
            "                \"newaddrfullname\": \"서울 강남구\"\n" +
            "            },\n" +
            "            \"addrbunho\": \"92\",\n" +
            "            \"addrdetail\": \"어넥스 B동 3층\"\n" +
            "        },\n" +
            "        \"homepage\": \"http://www.sushikoji.co.kr\",\n" +
            "        \"homepagenoprotocol\": \"www.sushikoji.co.kr\",\n" +
            "        \"wpointx\": 508095,\n" +
            "        \"wpointy\": 1117328\n" +
            "    }\n" +
            "}";
    private static final String NOT_EXIST_RESPONSE = "{\n" +
            "    \"isExist\": false\n" +
            "}";

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.close();
    }

    @Nested
    class getPlaceInfo {

        @Test
        void 매장_디테일_정보를_가져온다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(RESPONSE)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoPlaceClient kakaoPlaceClient = new KakaoPlaceClient(
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            PlaceInfo placeInfo = kakaoPlaceClient.getPlaceInfo(23829251L);

            // then
            assertThat(placeInfo.getPlaceId()).isEqualTo(23829251L);
            assertThat(placeInfo.getPlaceName()).isEqualTo("스시코우지");
            assertThat(placeInfo.getRoadAddress()).isEqualTo("서울 강남구 도산대로 318 어넥스 B동 3층");
            assertThat(placeInfo.getAddress()).isEqualTo("서울 강남구 논현동 92");
            assertThat(placeInfo.getX()).isEqualTo(508095);
            assertThat(placeInfo.getY()).isEqualTo(1117328);
        }

        @Test
        void 존재하지_않는_매장_디테일_정보를_가져오면_NotExistPlaceException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(NOT_EXIST_RESPONSE)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoPlaceClient kakaoPlaceClient = new KakaoPlaceClient(
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoPlaceClient.getPlaceInfo(23829251L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.NOT_EXIST_PLACE);
        }

        @Test
        void ResponseData가_올바르지_않으면_SearchMapsException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody("")
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoPlaceClient kakaoPlaceClient = new KakaoPlaceClient(
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoPlaceClient.getPlaceInfo(23829251L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SEARCH_MAPS);
        }
    }
}