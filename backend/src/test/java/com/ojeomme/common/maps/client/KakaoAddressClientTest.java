package com.ojeomme.common.maps.client;

import com.ojeomme.common.maps.entity.KakaoAddressCoord;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
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

class KakaoAddressClientTest {

    private MockWebServer mockWebServer;

    private static final String RESPONSE_DATA = "{\n" +
            "  \"documents\": [\n" +
            "    {\n" +
            "      \"address\": {\n" +
            "        \"address_name\": \"서울 강남구 논현동\",\n" +
            "        \"b_code\": \"1168010800\",\n" +
            "        \"h_code\": \"\",\n" +
            "        \"main_address_no\": \"\",\n" +
            "        \"mountain_yn\": \"N\",\n" +
            "        \"region_1depth_name\": \"서울\",\n" +
            "        \"region_2depth_name\": \"강남구\",\n" +
            "        \"region_3depth_h_name\": \"\",\n" +
            "        \"region_3depth_name\": \"논현동\",\n" +
            "        \"sub_address_no\": \"\",\n" +
            "        \"x\": \"127.030154778539\",\n" +
            "        \"y\": \"37.5126451506882\"\n" +
            "      },\n" +
            "      \"address_name\": \"서울 강남구 논현동\",\n" +
            "      \"address_type\": \"REGION\",\n" +
            "      \"road_address\": null,\n" +
            "      \"x\": \"127.030154778539\",\n" +
            "      \"y\": \"37.5126451506882\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"meta\": {\n" +
            "    \"is_end\": false,\n" +
            "    \"pageable_count\": 6,\n" +
            "    \"total_count\": 6\n" +
            "  }\n" +
            "}";
    private static final String NOT_EXIST_RESPONSE_DATA = "{\n" +
            "  \"documents\": [\n" +
            "  ],\n" +
            "  \"meta\": {\n" +
            "    \"is_end\": false,\n" +
            "    \"pageable_count\": 0,\n" +
            "    \"total_count\": 0\n" +
            "  }\n" +
            "}";
    private static final String ERROR_CODE_RESPONSE = "{\n" +
            "  \"code\": -2,\n" +
            "  \"msg\": \"The input parameter value is not in the service area\"\n" +
            "}";
    private static final String ERROR_TYPE_RESPONSE = "{\n" +
            "  \"errorType\": \"MissingParameter\",\n" +
            "  \"message\": \"query parameter required\"\n" +
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
    class getKakaoAddressCoord {

        private final String address = "서울 강남구 논현동";

        @Test
        void 주소의_좌표를_가져온다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(RESPONSE_DATA)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoAddressClient kakaoAddressClient = new KakaoAddressClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            KakaoAddressCoord kakaoAddressCoord = kakaoAddressClient.getKakaoAddressCoord(address);

            // then
            assertThat(kakaoAddressCoord.getX()).isEqualTo("127.030154778539");
            assertThat(kakaoAddressCoord.getY()).isEqualTo("37.5126451506882");
        }

        @Test
        void 존재하지_않는_주소를_가져오면_KakaoNotExistAddressException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(NOT_EXIST_RESPONSE_DATA)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoAddressClient kakaoAddressClient = new KakaoAddressClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoAddressClient.getKakaoAddressCoord(address));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_NOE_EXIST_ADDRESS);
        }

        @Test
        void 카카오에서_ErrorCode가_오면_KakaoSearchAddressException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setStatus("HTTP/1.1 400")
                    .setBody(ERROR_CODE_RESPONSE)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoAddressClient kakaoAddressClient = new KakaoAddressClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoAddressClient.getKakaoAddressCoord(address));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_SEARCH_ADDRESS);
        }

        @Test
        void 카카오에서_ErrorType이_오면_KakaoSearchAddressException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setStatus("HTTP/1.1 400")
                    .setBody(ERROR_TYPE_RESPONSE)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoAddressClient kakaoAddressClient = new KakaoAddressClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoAddressClient.getKakaoAddressCoord(address));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_SEARCH_ADDRESS);
        }

        @Test
        void ResponseData가_올바르지_않으면_KakaoSearchAddressException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody("")
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoAddressClient kakaoAddressClient = new KakaoAddressClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoAddressClient.getKakaoAddressCoord(address));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_SEARCH_ADDRESS);
        }
    }
}