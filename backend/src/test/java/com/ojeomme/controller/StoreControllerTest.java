package com.ojeomme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class StoreControllerTest extends AcceptanceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RegionCodeRepository regionCodeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private KakaoKeywordClient kakaoKeywordClient;

    private MockWebServer mockWebServer;

    @Nested
    class getStoreReviews {

        @Test
        void 매장과_리뷰를_가져온다() {
            // given
            Category category = categoryRepository.save(Category.builder()
                    .categoryName("초밥,롤")
                    .categoryDepth(1)
                    .build());

            Store store = Store.builder()
                    .kakaoPlaceId(23829251L)
                    .category(category)
                    .regionCode(regionCodeRepository.findById("1168010800").orElseThrow())
                    .storeName("스시코우지")
                    .addressName("서울 강남구 논현동 92")
                    .roadAddressName("서울 강남구 도산대로 318")
                    .x(508095)
                    .y(1117328)
                    .likeCnt(5)
                    .build();

            Review review1 = Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(4)
                    .content("리뷰1")
                    .revisitYn(false)
                    .build();
            Set<ReviewImage> reviewImages1 = Set.of(
                    ReviewImage.builder().review(review1).imageUrl("http://localhost:4000/image1").build(),
                    ReviewImage.builder().review(review1).imageUrl("http://localhost:4000/image2").build()
            );
            Set<ReviewRecommend> reviewRecommends1 = Set.of(
                    ReviewRecommend.builder().review(review1).recommendType(RecommendType.TASTE).build()
            );
            review1.addImages(reviewImages1);
            review1.addRecommends(reviewRecommends1);

            Review review2 = Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(5)
                    .content("리뷰2")
                    .revisitYn(true)
                    .build();
            Set<ReviewImage> reviewImages2 = Set.of();
            Set<ReviewRecommend> reviewRecommends2 = Set.of(
                    ReviewRecommend.builder().review(review2).recommendType(RecommendType.TASTE).build(),
                    ReviewRecommend.builder().review(review2).recommendType(RecommendType.VALUE_FOR_MONEY).build()
            );
            review2.addImages(reviewImages2);
            review2.addRecommends(reviewRecommends2);

            store.writeReview(review1);
            store.writeReview(review2);

            store = storeRepository.save(store);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/store/{storeId}", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(jsonPath.getLong("store.storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("store.placeId")).isEqualTo(store.getKakaoPlaceId());
            assertThat(jsonPath.getString("store.storeName")).isEqualTo(store.getStoreName());
            assertThat(jsonPath.getString("store.categoryName")).isEqualTo(store.getCategory().getCategoryName());
            assertThat(jsonPath.getString("store.addressName")).isEqualTo(store.getAddressName());
            assertThat(jsonPath.getString("store.roadAddressName")).isEqualTo(store.getRoadAddressName());
            assertThat(jsonPath.getInt("store.likeCnt")).isEqualTo(store.getLikeCnt());

            assertThat(jsonPath.getList("previewImages").size()).isEqualTo(2);

            assertThat(jsonPath.getList("reviews").size()).isEqualTo(store.getReviews().size());
            for (int i = 0; i < jsonPath.getList("reviews").size(); i++) {
                assertThat(jsonPath.getLong("reviews[" + i + "].reviewId")).isEqualTo(store.getReviews().get(store.getReviews().size() - i - 1).getId());
                assertThat(jsonPath.getString("reviews[" + i + "].nickname")).isEqualTo(store.getReviews().get(store.getReviews().size() - i - 1).getUser().getNickname());
                assertThat(jsonPath.getInt("reviews[" + i + "].starScore")).isEqualTo(store.getReviews().get(store.getReviews().size() - i - 1).getStarScore());
                assertThat(jsonPath.getString("reviews[" + i + "].content")).isEqualTo(store.getReviews().get(store.getReviews().size() - i - 1).getContent());
                assertThat(jsonPath.getBoolean("reviews[" + i + "].revisitYn")).isEqualTo(store.getReviews().get(store.getReviews().size() - i - 1).isRevisitYn());
                assertThat(CollectionUtils.isEqualCollection(
                                jsonPath.getList("reviews[" + i + "].images"),
                                store.getReviews().get(store.getReviews().size() - i - 1).getReviewImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toSet())
                        )
                ).isTrue();
                assertThat(CollectionUtils.isEqualCollection(
                                jsonPath.getList("reviews[" + i + "].recommends"),
                                store.getReviews().get(store.getReviews().size() - i - 1).getReviewRecommends().stream().map(v -> v.getRecommendType().getCode()).collect(Collectors.toSet())
                        )
                ).isTrue();
            }
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
    class searchKakaoPlaceList {

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
                    .x(508095)
                    .y(1117328)
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
            assertThat(jsonPath.getBoolean("meta.isEnd")).isFalse();

            assertThat(jsonPath.getString("places[0].storeId")).isEqualTo(store.getId().toString());
            assertThat(jsonPath.getString("places[0].placeId")).isEqualTo("23829251");
            assertThat(jsonPath.getString("places[0].placeName")).isEqualTo("스시코우지");
            assertThat(jsonPath.getString("places[0].categoryName")).isEqualTo("초밥,롤");
            assertThat(jsonPath.getString("places[0].phone")).isEqualTo("02-541-6200");
            assertThat(jsonPath.getString("places[0].addressName")).isEqualTo("서울 강남구 논현동 92");
            assertThat(jsonPath.getString("places[0].roadAddressName")).isEqualTo("서울 강남구 도산대로 318");
            assertThat(jsonPath.getString("places[0].x")).isEqualTo("127.03662909986537");
            assertThat(jsonPath.getString("places[0].y")).isEqualTo("37.52186058560857");
            assertThat(jsonPath.getInt("places[0].likeCnt")).isEqualTo(5);
            assertThat(jsonPath.getInt("places[0].reviewCnt")).isEqualTo(1);

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
            assertThat(jsonPath.getBoolean("meta.isEnd")).isFalse();

            assertThat(jsonPath.getString("places[0].storeId")).isBlank();
            assertThat(jsonPath.getString("places[0].placeId")).isEqualTo("23829251");
            assertThat(jsonPath.getString("places[0].placeName")).isEqualTo("스시코우지");
            assertThat(jsonPath.getString("places[0].categoryName")).isEqualTo("초밥,롤");
            assertThat(jsonPath.getString("places[0].phone")).isEqualTo("02-541-6200");
            assertThat(jsonPath.getString("places[0].addressName")).isEqualTo("서울 강남구 논현동 92");
            assertThat(jsonPath.getString("places[0].roadAddressName")).isEqualTo("서울 강남구 도산대로 318");
            assertThat(jsonPath.getString("places[0].x")).isEqualTo("127.03662909986537");
            assertThat(jsonPath.getString("places[0].y")).isEqualTo("37.52186058560857");
            assertThat(jsonPath.getInt("places[0].likeCnt")).isEqualTo(0);
            assertThat(jsonPath.getInt("places[0].reviewCnt")).isEqualTo(0);

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