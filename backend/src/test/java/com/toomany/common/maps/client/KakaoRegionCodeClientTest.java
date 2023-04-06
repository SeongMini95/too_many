package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.KakaoRegionCode;
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

class KakaoRegionCodeClientTest {

    private MockWebServer mockWebServer;

    private static final String RESPONSE = "{\n" +
            "  \"meta\": {\n" +
            "    \"total_count\": 2\n" +
            "  },\n" +
            "  \"documents\": [\n" +
            "    {\n" +
            "      \"region_type\": \"B\",\n" +
            "      \"code\": \"1168010800\",\n" +
            "      \"address_name\": \"서울특별시 강남구 논현동\",\n" +
            "      \"region_1depth_name\": \"서울특별시\",\n" +
            "      \"region_2depth_name\": \"강남구\",\n" +
            "      \"region_3depth_name\": \"논현동\",\n" +
            "      \"region_4depth_name\": \"\",\n" +
            "      \"x\": 127.02856630406664,\n" +
            "      \"y\": 37.511521092235625\n" +
            "    },\n" +
            "    {\n" +
            "      \"region_type\": \"H\",\n" +
            "      \"code\": \"1168053100\",\n" +
            "      \"address_name\": \"서울특별시 강남구 논현2동\",\n" +
            "      \"region_1depth_name\": \"서울특별시\",\n" +
            "      \"region_2depth_name\": \"강남구\",\n" +
            "      \"region_3depth_name\": \"논현2동\",\n" +
            "      \"region_4depth_name\": \"\",\n" +
            "      \"x\": 127.03735990690524,\n" +
            "      \"y\": 37.51739318345911\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private static final String COUNT_ZERO_RESPONSE = "{\n" +
            "  \"meta\": {\n" +
            "    \"total_count\": 0\n" +
            "  },\n" +
            "  \"documents\": [\n" +
            "  ]\n" +
            "}";
    private static final String NOT_EXIST_B_RESPONSE = "{\n" +
            "  \"meta\": {\n" +
            "    \"total_count\": 1\n" +
            "  },\n" +
            "  \"documents\": [\n" +
            "    {\n" +
            "      \"region_type\": \"H\",\n" +
            "      \"code\": \"1168053100\",\n" +
            "      \"address_name\": \"서울특별시 강남구 논현2동\",\n" +
            "      \"region_1depth_name\": \"서울특별시\",\n" +
            "      \"region_2depth_name\": \"강남구\",\n" +
            "      \"region_3depth_name\": \"논현2동\",\n" +
            "      \"region_4depth_name\": \"\",\n" +
            "      \"x\": 127.03735990690524,\n" +
            "      \"y\": 37.51739318345911\n" +
            "    }\n" +
            "  ]\n" +
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
    class getKakaoRegionCode {

        @Test
        void 행정구역_정보를_가져온다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoRegionCodeClient kakaoRegionCodeClient = new KakaoRegionCodeClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            KakaoRegionCode kakaoRegionCode = kakaoRegionCodeClient.getRegionCode("127", "34");

            // then
            assertThat(kakaoRegionCode.getCode()).isEqualTo("1168010800");
        }

        @Test
        void 카카오에서_ErrorCode가_오면_KakaoSearchRegionCodeException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setStatus("HTTP/1.1 400")
                    .setBody(ERROR_CODE_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            );

            KakaoRegionCodeClient kakaoRegionCodeClient = new KakaoRegionCodeClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoRegionCodeClient.getRegionCode("127", "34"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_SEARCH_REGION_CODE);
            assertThat(exception.getCause().getMessage()).isEqualTo("The input parameter value is not in the service area");
        }

        @Test
        void 카카오에서_ErrorType이_오면_KakaoSearchRegionCodeException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setStatus("HTTP/1.1 400")
                    .setBody(ERROR_TYPE_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoRegionCodeClient kakaoRegionCodeClient = new KakaoRegionCodeClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoRegionCodeClient.getRegionCode("127", "34"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_SEARCH_REGION_CODE);
            assertThat(exception.getCause().getMessage()).isEqualTo("query parameter required");
        }

        @Test
        void ResponseData가_올바르지_않으면_KakaoSearchRegionCodeException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody("")
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoRegionCodeClient kakaoRegionCodeClient = new KakaoRegionCodeClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoRegionCodeClient.getRegionCode("127", "34"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_SEARCH_REGION_CODE);
        }

        @Test
        void 검색된_ResponseData가_없으면_KakaoNotExistRegionCodeException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(COUNT_ZERO_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoRegionCodeClient kakaoRegionCodeClient = new KakaoRegionCodeClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoRegionCodeClient.getRegionCode("127", "34"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_NOT_EXIST_REGION_CODE);
        }

        @Test
        void 법정동이_없는_ResponseData면_KakaoNotExistRegionCodeException를_발생한다() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBody(NOT_EXIST_B_RESPONSE)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

            KakaoRegionCodeClient kakaoRegionCodeClient = new KakaoRegionCodeClient(
                    "restId",
                    String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort()),
                    WebClient.create()
            );

            // when
            ApiException exception = assertThrows(ApiException.class, () -> kakaoRegionCodeClient.getRegionCode("127", "34"));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_NOT_EXIST_REGION_CODE);
        }
    }
}