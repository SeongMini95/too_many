package com.toomany.controller;

import com.toomany.common.maps.client.KakaoRegionCodeClient;
import com.toomany.controller.support.AcceptanceTest;
import com.toomany.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class RegionControllerTest extends AcceptanceTest {

    @SpyBean
    private KakaoRegionCodeClient kakaoRegionCodeClient;

    private MockWebServer mockWebServer;

    @Nested
    class getRegionCodeOfCoord {

        @Test
        void 좌표의_지역_코드를_가져온다_depth4() throws Exception {
            // given
            startMockWebServer("2671025321");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/coord?x={x}&y={y}", "127", "34")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.asString()).isEqualTo("2671025300");
        }

        @Test
        void 좌표의_지역_코드를_가져온다_depth4x() throws Exception {
            // given
            startMockWebServer("2671025300");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/coord?x={x}&y={y}", "127", "34")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.asString()).isEqualTo("2671025300");
        }

        @Test
        void region_code가_없으면_RegionCodeNotFound를_발생한다() throws Exception {
            // given
            startMockWebServer("9999999999");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/coord?x={x}&y={y}", "127", "34")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getMessage());
        }
    }

    private void startMockWebServer(String code) throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String uri = String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort());
        ReflectionTestUtils.setField(kakaoRegionCodeClient, "regionCodeClient", WebClient.create().mutate().baseUrl(uri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

        String response = "{\n" +
                "  \"meta\": {\n" +
                "    \"total_count\": 2\n" +
                "  },\n" +
                "  \"documents\": [\n" +
                "    {\n" +
                "      \"region_type\": \"B\",\n" +
                "      \"code\": \"" + code + "\",\n" +
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
        mockWebServer.enqueue(new MockResponse()
                .setBody(response)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    }

    private void closeMockWebServer() throws Exception {
        mockWebServer.close();
    }
}