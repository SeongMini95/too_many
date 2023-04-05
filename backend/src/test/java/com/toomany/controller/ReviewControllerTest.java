package com.toomany.controller;

import com.toomany.common.maps.client.KakaoMapsClient;
import com.toomany.common.maps.client.KakaoPlaceClient;
import com.toomany.common.maps.client.KakaoRegionCodeClient;
import com.toomany.controller.support.AcceptanceTest;
import com.toomany.domain.category.Category;
import com.toomany.domain.category.repository.CategoryRepository;
import com.toomany.domain.regioncode.RegionCode;
import com.toomany.domain.regioncode.repository.RegionCodeRepository;
import com.toomany.domain.review.Review;
import com.toomany.domain.store.Store;
import com.toomany.domain.store.repository.StoreRepository;
import com.toomany.domain.user.repository.UserRepository;
import com.toomany.dto.request.review.WriteReviewRequestDto;
import com.toomany.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewControllerTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RegionCodeRepository regionCodeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private KakaoPlaceClient kakaoPlaceClient;

    @SpyBean
    private KakaoMapsClient kakaoMapsClient;

    @SpyBean
    private KakaoRegionCodeClient kakaoRegionCodeClient;

    private MockWebServer placeWebServer;
    private MockWebServer mapsWebServer;
    private MockWebServer regionCodeWebServer;

    @Nested
    class writeReview {

        private final WriteReviewRequestDto requestDto = WriteReviewRequestDto.builder()
                .placeId(23829251L)
                .revisitYn(true)
                .content("리뷰 작성 리뷰 작성 리뷰 작성")
                .images(List.of("http://localhost:4000/image1.jpg"))
                .recommends(List.of("1", "2"))
                .x("127.03662909986537")
                .y("37.52186058560857")
                .build();

        private final String code = "1168010800";

        @Test
        void 등록이_안된_store_존재하지_않는_category() throws Exception {
            // given
            startMockWebServer();

            setPlaceWebServer();
            setMapsWebServer(true);
            setRegionCodeWebServer(code);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.as(Long.class)).isNotNull();

            closeMockWebServer();
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            userRepository.delete(user);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        void 매장을_찾을_수_없으면_NotExistPlace를_발생한다() throws Exception {
            // given
            startMockWebServer();

            setPlaceWebServer();
            setMapsWebServer(false);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.NOT_EXIST_PLACE.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.NOT_EXIST_PLACE.getMessage());

            closeMockWebServer();
        }

        @Test
        void 등록이_안된_store_존재하는_category() throws Exception {
            startMockWebServer();

            setPlaceWebServer();
            setMapsWebServer(true);
            setRegionCodeWebServer(code);

            categoryRepository.save(Category.builder()
                    .categoryDepth(2)
                    .categoryName("초밥,롤")
                    .build());

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.as(Long.class)).isNotNull();

            closeMockWebServer();
        }

        @Test
        void category가_null이지만_상위_category는_존재() throws Exception {
            startMockWebServer();

            setPlaceWebServer();
            setMapsWebServer(true);
            setRegionCodeWebServer(code);

            categoryRepository.save(Category.builder()
                    .categoryDepth(1)
                    .categoryName("일식")
                    .build());

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.as(Long.class)).isNotNull();

            closeMockWebServer();
        }

        @Test
        void region_code가_없으면_RegionNotFoundException를_발생한다() throws Exception {
            // given
            startMockWebServer();

            setPlaceWebServer();
            setMapsWebServer(true);
            setRegionCodeWebServer("9999999999");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getMessage());

            closeMockWebServer();
        }

        @Test
        void store가_존재할_때() throws Exception {
            // given
            startMockWebServer();

            setPlaceWebServer();
            setMapsWebServer(true);
            setRegionCodeWebServer(code);

            Category category = categoryRepository.save(Category.builder()
                    .categoryDepth(1)
                    .categoryName("일식")
                    .build());
            RegionCode regionCode = regionCodeRepository.findById(code).orElseThrow();
            Store store = Store.builder()
                    .kakaoPlaceId(23829251L)
                    .category(category)
                    .regionCode(regionCode)
                    .storeName("스시코우지")
                    .addressName("서울 강남구 논현동 92")
                    .roadAddressName("서울 강남구 도산대로 318")
                    .x(508095)
                    .y(1117328)
                    .likeCnt(5)
                    .build();
            store.writeReview(Review.builder()
                    .store(store)
                    .user(user)
                    .content("리뷰 작성")
                    .revisitYn(true)
                    .build());
            storeRepository.save(store);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.as(Long.class)).isNotNull();

            closeMockWebServer();
        }
    }

    private void startMockWebServer() throws Exception {
        placeWebServer = new MockWebServer();
        mapsWebServer = new MockWebServer();
        regionCodeWebServer = new MockWebServer();

        placeWebServer.start();
        mapsWebServer.start();
        regionCodeWebServer.start();
    }

    private void closeMockWebServer() throws Exception {
        placeWebServer.close();
        mapsWebServer.close();
        regionCodeWebServer.close();
    }

    private void setPlaceWebServer() {
        String uri = String.format("http://%s:%s", placeWebServer.getHostName(), placeWebServer.getPort());
        ReflectionTestUtils.setField(kakaoPlaceClient, "placeClient", WebClient.create().mutate().baseUrl(uri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

        String response = "{\n" +
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

        placeWebServer.enqueue(new MockResponse()
                .setBody(response)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }

    private void setMapsWebServer(boolean success) {
        String uri = String.format("http://%s:%s", mapsWebServer.getHostName(), mapsWebServer.getPort());
        ReflectionTestUtils.setField(kakaoMapsClient, "mapsClient", WebClient.create().mutate().baseUrl(uri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

        String successResponse = "{\n" +
                "  \"documents\": [\n" +
                "    {\n" +
                "      \"address_name\": \"서울 강남구 논현동 92\",\n" +
                "      \"category_group_code\": \"FD6\",\n" +
                "      \"category_group_name\": \"음식점\",\n" +
                "      \"category_name\": \"음식점 > 일식 > 초밥,롤\",\n" +
                "      \"distance\": \"\",\n" +
                "      \"id\": \"23829251\",\n" +
                "      \"phone\": \"02-541-6200\",\n" +
                "      \"place_name\": \"스시코우지\",\n" +
                "      \"place_url\": \"http://place.map.kakao.com/23829251\",\n" +
                "      \"road_address_name\": \"서울 강남구 도산대로 318\",\n" +
                "      \"x\": \"127.03662909986537\",\n" +
                "      \"y\": \"37.52186058560857\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"meta\": {\n" +
                "    \"is_end\": true,\n" +
                "    \"pageable_count\": 1,\n" +
                "    \"same_name\": {\n" +
                "      \"keyword\": \"스시코우지\",\n" +
                "      \"region\": [],\n" +
                "      \"selected_region\": \"\"\n" +
                "    },\n" +
                "    \"total_count\": 1\n" +
                "  }\n" +
                "}";
        String failResponse = "{\n" +
                "  \"documents\": [\n" +
                "  ],\n" +
                "  \"meta\": {\n" +
                "    \"is_end\": true,\n" +
                "    \"pageable_count\": 0,\n" +
                "    \"same_name\": {\n" +
                "      \"keyword\": \"스시코우지\",\n" +
                "      \"region\": [],\n" +
                "      \"selected_region\": \"\"\n" +
                "    },\n" +
                "    \"total_count\": 0\n" +
                "  }\n" +
                "}";

        if (success) {
            mapsWebServer.enqueue(new MockResponse()
                    .setBody(successResponse)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        } else {
            mapsWebServer.enqueue(new MockResponse()
                    .setBody(failResponse)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        }
    }

    public void setRegionCodeWebServer(String code) {
        String uri = String.format("http://%s:%s", regionCodeWebServer.getHostName(), regionCodeWebServer.getPort());
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
        regionCodeWebServer.enqueue(new MockResponse()
                .setBody(response)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}