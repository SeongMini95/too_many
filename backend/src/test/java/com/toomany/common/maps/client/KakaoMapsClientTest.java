package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.PlaceList;
import com.toomany.dto.request.store.SearchStoreRequestDto;
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

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KakaoMapsClientTest {

    private MockWebServer mockWebServer;

    private static final String[] IDS = {"977331811", "1315083198"};
    private static final String[] PLACE_NAMES = {"스시소라 대치점", "스시소라 광화문점"};
    private static final String KEYWORD_SEARCH_RESPONSE = "{\n" +
            "  \"documents\": [\n" +
            "    {\n" +
            "      \"address_name\": \"서울 강남구 대치동 894\",\n" +
            "      \"category_group_code\": \"FD6\",\n" +
            "      \"category_group_name\": \"음식점\",\n" +
            "      \"category_name\": \"음식점 > 일식 > 초밥,롤\",\n" +
            "      \"distance\": \"\",\n" +
            "      \"id\": \"" + IDS[0] + "\",\n" +
            "      \"phone\": \"02-567-8200\",\n" +
            "      \"place_name\": \"" + PLACE_NAMES[0] + "\",\n" +
            "      \"place_url\": \"http://place.map.kakao.com/977331811\",\n" +
            "      \"road_address_name\": \"서울 강남구 삼성로85길 33\",\n" +
            "      \"x\": \"127.054819929805\",\n" +
            "      \"y\": \"37.5042821628439\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"address_name\": \"서울 종로구 청진동 146\",\n" +
            "      \"category_group_code\": \"FD6\",\n" +
            "      \"category_group_name\": \"음식점\",\n" +
            "      \"category_name\": \"음식점 > 일식 > 초밥,롤\",\n" +
            "      \"distance\": \"\",\n" +
            "      \"id\": \"" + IDS[1] + "\",\n" +
            "      \"phone\": \"02-733-8400\",\n" +
            "      \"place_name\": \"" + PLACE_NAMES[1] + "\",\n" +
            "      \"place_url\": \"http://place.map.kakao.com/1315083198\",\n" +
            "      \"road_address_name\": \"서울 종로구 종로 19\",\n" +
            "      \"x\": \"126.9798533355086\",\n" +
            "      \"y\": \"37.570563545503084\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"meta\": {\n" +
            "    \"is_end\": false,\n" +
            "    \"pageable_count\": 2,\n" +
            "    \"same_name\": {\n" +
            "      \"keyword\": \"스시소라\",\n" +
            "      \"region\": [],\n" +
            "      \"selected_region\": \"\"\n" +
            "    },\n" +
            "    \"total_count\": 2\n" +
            "  }\n" +
            "}";
    private static final String ERROR_RESPONSE = "{\n" +
            "  \"errorType\": \"MissingParameter\",\n" +
            "  \"message\": \"query parameter required\"\n" +
            "}";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    class getPlace {

        private final SearchStoreRequestDto requestDto = SearchStoreRequestDto.builder()
                .query("스시소라")
                .x("")
                .y("")
                .page(1)
                .build();

        @Test
        void 키워드로_장소를_검색한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(KEYWORD_SEARCH_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoMapsClient kakaoMapsClient = new KakaoMapsClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            PlaceList placeList = kakaoMapsClient.getPlace(requestDto);

            // then
            for (int i = 0; i < IDS.length; i++) {
                assertThat(placeList.getDocuments().get(i).getId()).isEqualTo(IDS[i]);
                assertThat(placeList.getDocuments().get(i).getPlaceName()).isEqualTo(PLACE_NAMES[i]);
            }
        }

        @Test
        void 카카오에서_ErrorCode가_오면_SearchMapsException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setStatus("HTTP/1.1 400")
                    .setBody(ERROR_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoMapsClient kakaoMapsClient = new KakaoMapsClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoMapsClient.getPlace(requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SEARCH_MAPS);
        }

        @Test
        void ResponseData가_올바르지_않으면_SearchMapsException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody("")
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoMapsClient kakaoMapsClient = new KakaoMapsClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoMapsClient.getPlace(requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.SEARCH_MAPS);
        }
    }
}