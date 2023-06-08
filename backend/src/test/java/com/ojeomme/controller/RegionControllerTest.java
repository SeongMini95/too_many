package com.ojeomme.controller;

import com.ojeomme.common.maps.client.KakaoAddressClient;
import com.ojeomme.common.maps.client.KakaoRegionCodeClient;
import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegionControllerTest extends AcceptanceTest {

    @SpyBean
    private KakaoRegionCodeClient kakaoRegionCodeClient;

    @SpyBean
    private KakaoAddressClient kakaoAddressClient;

    private MockWebServer mockWebServer;

    @Nested
    class getRegionCodeOfCoord {

        @Test
        void 좌표의_지역_코드를_가져온다_depth4() throws Exception {
            // given
            startRegionServer("2671025321");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/regionOfCoord?x={x}&y={y}", "127", "34")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("codes")).isEqualTo(List.of("2600000000", "2671000000", "2671025300"));
            assertThat(jsonPath.getString("address")).isEqualTo("부산광역시 기장군 장안읍");

            closeMockWebServer();
        }

        @Test
        void 좌표의_지역_코드를_가져온다_depth4x() throws Exception {
            // given
            startRegionServer("2671025300");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/regionOfCoord?x={x}&y={y}", "127", "34")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("codes")).isEqualTo(List.of("2600000000", "2671000000", "2671025300"));
            assertThat(jsonPath.getString("address")).isEqualTo("부산광역시 기장군 장안읍");

            closeMockWebServer();
        }

        @Test
        void region_code가_없으면_RegionCodeNotFound를_발생한다() throws Exception {
            // given
            startRegionServer("9999999999");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/regionOfCoord?x={x}&y={y}", "127", "34")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getMessage());

            closeMockWebServer();
        }
    }

    @Nested
    class getCoordOfRegionCode {

        @Test
        void 지역의_좌표를_가져온다() throws Exception {
            // given
            startAddressServer();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/coordOfRegion?code={code}", "1168010800")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("x")).isEqualTo("127.030154778539");
            assertThat(jsonPath.getString("y")).isEqualTo("37.5126451506882");

            closeMockWebServer();
        }

        @Test
        void 지역의_좌표를_가져온다_만약에_depth가_4면_상위_코드로() throws Exception {
            // given
            startAddressServer();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/coordOfRegion?code={code}", "2671025021")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("x")).isEqualTo("127.030154778539");
            assertThat(jsonPath.getString("y")).isEqualTo("37.5126451506882");

            closeMockWebServer();
        }

        @Test
        void 초기_지역_코드가_존재하지_않으면_RegionCodeNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/coordOfRegion?code={code}", "9999999999")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class getRegionCodeList {

        @Test
        void 지역코드의_이름과_코드를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/region/list")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }
    }

    private void startRegionServer(String code) throws Exception {
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

    private void startAddressServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String uri = String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort());
        ReflectionTestUtils.setField(kakaoAddressClient, "addressClient", WebClient.create().mutate().baseUrl(uri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

        String response = "{\n" +
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
        mockWebServer.enqueue(new MockResponse()
                .setBody(response)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    }

    private void closeMockWebServer() throws Exception {
        mockWebServer.close();
    }
}