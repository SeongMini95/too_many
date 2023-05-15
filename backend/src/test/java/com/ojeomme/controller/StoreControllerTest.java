package com.ojeomme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.review.repository.ReviewRepository;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.storelikelog.StoreLikeLog;
import com.ojeomme.domain.storelikelog.repository.StoreLikeLogRepository;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StoreControllerTest extends AcceptanceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RegionCodeRepository regionCodeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreLikeLogRepository storeLikeLogRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @SpyBean
    private KakaoKeywordClient kakaoKeywordClient;

    private MockWebServer mockWebServer;

    @Nested
    class getStoreList {

        @Test
        void 매장_목록을_가져온다() {
            // given
            for (int i = 0; i < 5; i++) {
                Store store = createStore();
                createReview(store, i, i);
            }

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .param("regionCode", "1111010100")
                    .param("category", store.getCategory().getId())
                    .when().get("/api/store/list")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 매장_목록을_가져온다_page가_0() {
            // given
            for (int i = 0; i < 20; i++) {
                Store store = createStore();
                createReview(store, i, i);
            }

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .param("regionCode", "1111010100")
                    .param("category", store.getCategory().getId())
                    .param("page", 0)
                    .when().get("/api/store/list")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 매장_목록을_가져온다_page가_1_이상() {
            // given
            for (int i = 0; i < 29; i++) {
                Store store = createStore();
                createReview(store, i, i);
            }

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .param("regionCode", "1111010100")
                    .param("category", store.getCategory().getId())
                    .param("page", "2")
                    .when().get("/api/store/list")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 카테고리가_null이다() {
            // given
            for (int i = 0; i < 5; i++) {
                Store store = createStore();
                createReview(store, i, i);
            }

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .param("regionCode", "1111010100")
                    .param("category", "")
                    .when().get("/api/store/list")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        private Store createStore() {
            return storeRepository.save(Store.builder()
                    .kakaoPlaceId(1315083198L)
                    .category(store.getCategory())
                    .regionCode(regionCodeRepository.findById("1111010100").orElseThrow())
                    .storeName(UUID.randomUUID().toString())
                    .addressName("주소")
                    .roadAddressName("도로명 주소")
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(0)
                    .mainImageUrl("http://localhost:4000/image.png")
                    .build());
        }

        private void createReview(Store store, int starScore, int likeCnt) {
            reviewRepository.save(Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(starScore)
                    .content("")
                    .revisitYn(false)
                    .likeCnt(likeCnt)
                    .build());
        }
    }

    @Nested
    class getTodayStoreRanking {

        @Test
        void 지역의_오늘의_추천_매장_가져온다() {
            // given
            for (int i = 0; i < 10; i++) {
                Store store = createStore();
                createReview(store, i % 5 + 1, i, true);
            }

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .param("regionCode", "1111010100")
                    .when().get("/api/store/todayRanking")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("stores")).hasSizeLessThanOrEqualTo(10);
        }

        @Test
        void 지역의_오늘의_추천_매장_가져온다_이미지_없음() {
            // given
            for (int i = 0; i < 10; i++) {
                Store store = createStore();
                createReview(store, i % 5 + 1, i, false);
            }

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .param("regionCode", "1111010100")
                    .when().get("/api/store/todayRanking")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("stores")).hasSizeLessThanOrEqualTo(10);
        }

        private Store createStore() {
            return storeRepository.save(Store.builder()
                    .kakaoPlaceId(1315083198L)
                    .category(store.getCategory())
                    .regionCode(regionCodeRepository.findById("1111010100").orElseThrow())
                    .storeName(UUID.randomUUID().toString())
                    .addressName("주소")
                    .roadAddressName("도로명 주소")
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(0)
                    .build());
        }

        private void createReview(Store store, int starScore, int likeCnt, boolean existImage) {
            Review review = Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(starScore)
                    .content("")
                    .revisitYn(false)
                    .likeCnt(likeCnt)
                    .build();

            if (existImage) {
                review.addImages(Set.of(
                        ReviewImage.builder()
                                .review(review)
                                .imageUrl(UUID.randomUUID() + ".png")
                                .build()));
            }

            reviewRepository.save(review);
        }
    }

    @Nested
    class getStoreLikeLogOfUser {

        @Test
        void 좋아요를_누른_매장이다() {
            // given
            StoreLikeLog storeLikeLog = StoreLikeLog.builder()
                    .store(store)
                    .user(user)
                    .build();
            storeLikeLog.setDateTime(LocalDateTime.now(), LocalDateTime.now());
            storeLikeLogRepository.save(storeLikeLog);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/store/{storeId}/like", store.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.as(Boolean.class)).isTrue();
        }

        @Test
        void 안누른_매장이다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/store/{storeId}/like", store.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.as(Boolean.class)).isFalse();
        }
    }

    @Nested
    class likeStore {

        @Test
        void 매장의_공감을_누른다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/store/{storeId}/like", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getBoolean("result")).isTrue();
            assertThat(jsonPath.getInt("likeCnt")).isEqualTo(store.getLikeCnt() + 1);
        }

        @Test
        void 이미_누른_매장이다() {
            // given
            StoreLikeLog storeLikeLog = StoreLikeLog.builder()
                    .store(store)
                    .user(user)
                    .build();
            storeLikeLog.setDateTime(LocalDateTime.now(), LocalDateTime.now());
            storeLikeLogRepository.save(storeLikeLog);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/store/{storeId}/like", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getBoolean("result")).isFalse();
            assertThat(jsonPath.getInt("likeCnt")).isEqualTo(store.getLikeCnt() - 1);
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/store/{storeId}/like", store.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        void 매장이_존재하지_않으면_StoreNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/store/{storeId}/like", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.STORE_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.STORE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class getStore {

        @Test
        void 매장_정보를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/store/{storeId}", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(jsonPath.getLong("storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("placeId")).isEqualTo(store.getKakaoPlaceId());
            assertThat(jsonPath.getString("storeName")).isEqualTo(store.getStoreName());
            assertThat(jsonPath.getString("categoryName")).isEqualTo(store.getCategory().getCategoryName());
            assertThat(jsonPath.getString("addressName")).isEqualTo(store.getAddressName());
            assertThat(jsonPath.getString("roadAddressName")).isEqualTo(store.getRoadAddressName());
            assertThat(jsonPath.getString("x")).isEqualTo(store.getX());
            assertThat(jsonPath.getString("y")).isEqualTo(store.getY());
            assertThat(jsonPath.getInt("likeCnt")).isEqualTo(store.getLikeCnt());
            assertThat(jsonPath.getInt("reviewCnt")).isEqualTo(store.getReviews().size());
            assertThat(jsonPath.getDouble("avgStarScore")).isEqualTo(store.getReviews().stream().mapToDouble(Review::getStarScore).average().orElse(0));
            assertThat(jsonPath.getBoolean("isLike")).isFalse();
        }

        @Test
        void 매장_정보를_가져온다_좋아요_누름() {
            // given
            StoreLikeLog storeLikeLog = StoreLikeLog.builder()
                    .store(store)
                    .user(user)
                    .build();
            storeLikeLog.setDateTime(LocalDateTime.now(), LocalDateTime.now());
            storeLikeLogRepository.save(storeLikeLog);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/store/{storeId}", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(jsonPath.getLong("storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("placeId")).isEqualTo(store.getKakaoPlaceId());
            assertThat(jsonPath.getString("storeName")).isEqualTo(store.getStoreName());
            assertThat(jsonPath.getString("categoryName")).isEqualTo(store.getCategory().getCategoryName());
            assertThat(jsonPath.getString("addressName")).isEqualTo(store.getAddressName());
            assertThat(jsonPath.getString("roadAddressName")).isEqualTo(store.getRoadAddressName());
            assertThat(jsonPath.getString("x")).isEqualTo(store.getX());
            assertThat(jsonPath.getString("y")).isEqualTo(store.getY());
            assertThat(jsonPath.getInt("likeCnt")).isEqualTo(store.getLikeCnt());
            assertThat(jsonPath.getInt("reviewCnt")).isEqualTo(store.getReviews().size());
            assertThat(jsonPath.getDouble("avgStarScore")).isEqualTo(store.getReviews().stream().mapToDouble(Review::getStarScore).average().orElse(0));
            assertThat(jsonPath.getBoolean("isLike")).isTrue();
        }

        @Test
        void 매장이_없으면_StoreNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/store/{storeId}", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.STORE_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.STORE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class searchPlaceList {

        private final Map<String, Object> requestDto = new ObjectMapper().convertValue(SearchPlaceListRequestDto.builder()
                .query("스시코우지")
                .x("127.03662909986537")
                .y("37.52186058560857")
                .page(1)
                .build(), Map.class);

        @Test
        void 매장_목록을_가져온다() throws Exception {
            // given
            startMockWebServer();
            setMapsClinet();

            Category category = categoryRepository.save(Category.builder()
                    .categoryName("음식점")
                    .categoryDepth(1)
                    .build());

            Store store = Store.builder()
                    .kakaoPlaceId(23829251L)
                    .category(category)
                    .regionCode(regionCodeRepository.findById("1168010800").orElseThrow())
                    .storeName("스시코우지")
                    .addressName("서울 강남구 논현동 92")
                    .roadAddressName("서울 강남구 도산대로 318")
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .build();
            store.writeReview(Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(5)
                    .content("리뷰")
                    .revisitYn(false)
                    .build());
            store = storeRepository.save(store);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .params(requestDto)
                    .when().get("/api/store/searchPlaceList")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getInt("meta.totalCount")).isEqualTo(1);
            assertThat(jsonPath.getInt("meta.pageableCount")).isEqualTo(1);
            assertThat(jsonPath.getBoolean("meta.isEnd")).isTrue();

            assertThat(jsonPath.getLong("places[0].storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("places[0].placeId")).isEqualTo(23829251L);
            assertThat(jsonPath.getString("places[0].placeName")).isEqualTo("스시코우지");
            assertThat(jsonPath.getString("places[0].categoryName")).isEqualTo("초밥,롤");
            assertThat(jsonPath.getString("places[0].phone")).isEqualTo("02-541-6200");
            assertThat(jsonPath.getString("places[0].addressName")).isEqualTo("서울 강남구 논현동 92");
            assertThat(jsonPath.getString("places[0].roadAddressName")).isEqualTo("서울 강남구 도산대로 318");
            assertThat(jsonPath.getString("places[0].x")).isEqualTo("127.03662909986537");
            assertThat(jsonPath.getString("places[0].y")).isEqualTo("37.52186058560857");

            closeMockWebServer();
        }

        @Test
        void 매장_목록을_가져온다_없는_매장() throws Exception {
            // given
            startMockWebServer();

            setMapsClinet();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .params(requestDto)
                    .when().get("/api/store/searchPlaceList")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getInt("meta.totalCount")).isEqualTo(1);
            assertThat(jsonPath.getInt("meta.pageableCount")).isEqualTo(1);
            assertThat(jsonPath.getBoolean("meta.isEnd")).isTrue();

            assertThat(jsonPath.getString("places[0].storeId")).isNull();
            assertThat(jsonPath.getLong("places[0].placeId")).isEqualTo(23829251L);
            assertThat(jsonPath.getString("places[0].placeName")).isEqualTo("스시코우지");
            assertThat(jsonPath.getString("places[0].categoryName")).isEqualTo("초밥,롤");
            assertThat(jsonPath.getString("places[0].phone")).isEqualTo("02-541-6200");
            assertThat(jsonPath.getString("places[0].addressName")).isEqualTo("서울 강남구 논현동 92");
            assertThat(jsonPath.getString("places[0].roadAddressName")).isEqualTo("서울 강남구 도산대로 318");
            assertThat(jsonPath.getString("places[0].x")).isEqualTo("127.03662909986537");
            assertThat(jsonPath.getString("places[0].y")).isEqualTo("37.52186058560857");

            closeMockWebServer();
        }
    }

    private void startMockWebServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    private void closeMockWebServer() throws Exception {
        mockWebServer.close();
    }

    private void setMapsClinet() {
        String uri = String.format("http://%s:%s", mockWebServer.getHostName(), mockWebServer.getPort());
        ReflectionTestUtils.setField(kakaoKeywordClient, "mapsClient", WebClient.create().mutate().baseUrl(uri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

        String response = "{\n" +
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
        mockWebServer.enqueue(new MockResponse()
                .setBody(response)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    }
}