package com.ojeomme.controller;

import com.ojeomme.common.enums.EnumCodeConverterUtils;
import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.client.KakaoPlaceClient;
import com.ojeomme.common.maps.client.KakaoRegionCodeClient;
import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.review.repository.ReviewRepository;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewlikelog.ReviewLikeLog;
import com.ojeomme.domain.reviewlikelog.repository.ReviewLikeLogRepository;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.domain.reviewrecommend.repository.ReviewRecommendRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.dto.request.review.ModifyReviewRequestDto;
import com.ojeomme.dto.request.review.WriteReviewRequestDto;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewControllerTest extends AcceptanceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RegionCodeRepository regionCodeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private KakaoPlaceClient kakaoPlaceClient;

    @SpyBean
    private KakaoKeywordClient kakaoKeywordClient;

    @SpyBean
    private KakaoRegionCodeClient kakaoRegionCodeClient;

    @Autowired
    private ReviewLikeLogRepository reviewLikeLogRepository;

    @Autowired
    private ReviewRecommendRepository reviewRecommendRepository;

    private static final String UPLOAD_PATH = "build/resources/test";

    private MockWebServer placeWebServer;
    private MockWebServer mapsWebServer;
    private MockWebServer regionCodeWebServer;

    @Nested
    class getRefreshReviewList {

        @Test
        void 최근_작성된_리뷰를_가져온다() {
            // given
            Review newReview = reviewRepository.save(Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(5)
                    .likeCnt(1)
                    .revisitYn(true)
                    .content("리뷰")
                    .build());

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .param("lastId", review.getId())
                    .when().get("/api/review/store/{storeId}/refresh", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("reviews[0].reviewId")).isEqualTo(newReview.getId());
            assertThat(jsonPath.getString("reviews[0].nickname")).isEqualTo(newReview.getUser().getNickname());
            assertThat(jsonPath.getInt("reviews[0].starScore")).isEqualTo(newReview.getStarScore());
            assertThat(jsonPath.getString("reviews[0].content")).isEqualTo(newReview.getContent());
            assertThat(jsonPath.getInt("reviews[0].likeCnt")).isEqualTo(newReview.getLikeCnt());
            assertThat(jsonPath.getInt("reviews[0].likeCnt")).isEqualTo(newReview.getLikeCnt());
            assertThat(jsonPath.getList("reviews[0].images")).hasSameSizeAs(newReview.getReviewImages());
            assertThat(jsonPath.getList("reviews[0].recommends")).hasSameSizeAs(newReview.getReviewRecommends());
            assertThat(jsonPath.getString("reviews[0].profile")).isEqualTo(newReview.getUser().getProfile());
        }
    }

    @Nested
    class getReview {

        @Test
        void 리뷰를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/review/{reviewId}", review.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("reviewId")).isEqualTo(review.getId());
            assertThat(jsonPath.getInt("starScore")).isEqualTo(review.getStarScore());
            assertThat(jsonPath.getString("content")).isEqualTo(review.getContent());
            assertThat(jsonPath.getList("images")).isEqualTo(review.getReviewImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList()));
            assertThat(jsonPath.getList("recommends")).hasSameSizeAs(review.getReviewRecommends());
        }

        @Test
        void 리뷰가_없으면_ReviewNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/review/{reviewId}", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class likeReview {

        @Test
        void 리뷰_좋아요를_누른다() {
            // given
            Review review = Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(4)
                    .content("리뷰1")
                    .revisitYn(false)
                    .likeCnt(10)
                    .build();
            review.addImages(Set.of(
                    ReviewImage.builder()
                            .review(review)
                            .imageUrl("http://localhost:4000/image.png")
                            .build()
            ));
            review = reviewRepository.save(review);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/review/{reviewId}/like", review.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getBoolean("result")).isTrue();
            assertThat(jsonPath.getInt("likeCnt")).isEqualTo(review.getLikeCnt() + 1);
        }

        @Test
        void 리뷰가_이미_존재한다() {
            // given
            Review review = reviewRepository.save(Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(4)
                    .content("리뷰1")
                    .revisitYn(false)
                    .likeCnt(1)
                    .build());
            ReviewLikeLog reviewLikeLog = ReviewLikeLog.builder()
                    .review(review)
                    .user(user)
                    .build();
            reviewLikeLog.setDateTime(LocalDateTime.now(), LocalDateTime.now());
            reviewLikeLogRepository.save(reviewLikeLog);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/review/{reviewId}/like", review.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getBoolean("result")).isFalse();
            assertThat(jsonPath.getInt("likeCnt")).isEqualTo(review.getLikeCnt() - 1);
        }

        @Test
        void 유저를_찾지못하면_UserNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/review/{reviewId}/like", review.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        void 리뷰를_찾지_못하면_ReviewNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/api/review/{reviewId}/like", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class deleteReview {

        @Test
        void 리뷰를_삭제한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().delete("/api/review/{reviewId}", review.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 리뷰가_존재하지_않으면_ReviewNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().delete("/api/review/{reviewId}", -1)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class modifyReview {

        @Test
        void 리뷰를_수정한다() throws Exception {
            // given
            Category category = categoryRepository.save(Category.builder()
                    .categoryDepth(1)
                    .categoryName("일식")
                    .build());
            RegionCode regionCode = regionCodeRepository.findById("1168010800").orElseThrow();
            Store store = Store.builder()
                    .kakaoPlaceId(23829251L)
                    .category(category)
                    .regionCode(regionCode)
                    .storeName("스시코우지")
                    .addressName("서울 강남구 논현동 92")
                    .roadAddressName("서울 강남구 도산대로 318")
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .build();
            Review review = Review.builder()
                    .user(user)
                    .store(store)
                    .starScore(1)
                    .content("리뷰1")
                    .revisitYn(false)
                    .likeCnt(0)
                    .build();
            review.addImages(Set.of(
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/2023/4/18/" + createImage(false) + ".png").build(),
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/2023/4/18/" + createImage(false) + ".png").build()
            ));
            review.addRecommends(Set.of(
                    ReviewRecommend.builder().review(review).recommendType(RecommendType.TASTE).build(),
                    ReviewRecommend.builder().review(review).recommendType(RecommendType.VALUE_FOR_MONEY).build()
            ));
            store.writeReview(review);
            store = storeRepository.save(store);

            ModifyReviewRequestDto requestDto = ModifyReviewRequestDto.builder()
                    .starScore(5)
                    .content("리뷰 테스트 123123123123")
                    .revisitYn(true)
                    .images(List.of(
                            review.getReviewImages().iterator().next().getImageUrl(),
                            "http://localhost:4000/temp/2023/4/18/" + createImage(true) + ".png"
                    ))
                    .recommends(List.of(1, 3))
                    .build();

            // when
            Review getReview = store.getReviews().get(0);

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/review/{reviewId}", getReview.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("reviewId")).isEqualTo(getReview.getId());
            assertThat(jsonPath.getInt("starScore")).isEqualTo(requestDto.getStarScore());
            assertThat(jsonPath.getString("content")).isEqualTo(requestDto.getContent());
            assertThat(jsonPath.getList("images")).hasSameSizeAs(requestDto.getImages());
            assertThat(jsonPath.getList("recommends")).hasSameSizeAs(requestDto.getRecommends());
        }

        @Test
        void 리뷰가_없으면_ReviewNotFoundException를_발생한다() throws Exception {
            // given
            ModifyReviewRequestDto requestDto = ModifyReviewRequestDto.builder()
                    .starScore(5)
                    .content("리뷰 테스트 123123123123")
                    .revisitYn(true)
                    .images(List.of(
                            review.getReviewImages().iterator().next().getImageUrl(),
                            "http://localhost:4000/temp/2023/4/18/" + createImage(true) + ".png"
                    ))
                    .recommends(List.of(1, 3))
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/review/{reviewId}", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND.getMessage());
        }

        private String createImage(boolean temp) throws Exception {
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            String filename = UUID.randomUUID() + "";
            File tempFile = Paths.get(UPLOAD_PATH, (temp ? "/temp" : "") + "/2023/4/18/" + filename + ".png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();

            return filename;
        }
    }

    @Nested
    class getReviewList {

        @Test
        void 리뷰_리스트를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/review/store/{storeId}", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LocalDate now = LocalDate.now();
            Map<RecommendType, Long> counting = reviewRecommendRepository.findAll().stream()
                    .collect(Collectors.groupingBy(ReviewRecommend::getRecommendType, Collectors.counting()));

            // then
            assertThat(jsonPath.getList("reviews")).hasSameSizeAs(store.getReviews());
            for (int i = 0; i < jsonPath.getList("reviews").size(); i++) {
                assertThat(jsonPath.getLong("reviews[" + i + "].reviewId")).isEqualTo(store.getReviews().get(i).getId());
                assertThat(jsonPath.getString("reviews[" + i + "].nickname")).isEqualTo(store.getReviews().get(i).getUser().getNickname());
                assertThat(jsonPath.getInt("reviews[" + i + "].starScore")).isEqualTo(store.getReviews().get(i).getStarScore());
                assertThat(jsonPath.getString("reviews[" + i + "].content")).isEqualTo(store.getReviews().get(i).getContent());
                assertThat(jsonPath.getInt("reviews[" + i + "].likeCnt")).isEqualTo(store.getReviews().get(i).getLikeCnt());
                assertThat(CollectionUtils.isEqualCollection(
                        jsonPath.getList("reviews[" + i + "].images"),
                        store.getReviews().get(i).getReviewImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList())
                )).isTrue();

                for (int j = 0; j < jsonPath.getList("reviews[" + i + "].recommends").size(); j++) {
                    System.out.println(jsonPath.getString("reviews[" + i + "].recommends[" + j + "].type") + " aaaaaaaaaaa");
                    assertThat(CollectionUtils.containsAny(
                            store.getReviews().get(i).getReviewRecommends().stream()
                                    .map(ReviewRecommend::getRecommendType)
                                    .collect(Collectors.toSet()),
                            EnumCodeConverterUtils.ofCode(jsonPath.getString("reviews[" + i + "].recommends[" + j + "].type"), RecommendType.class))
                    ).isTrue();
                }

                assertThat(jsonPath.getString("reviews[" + i + "].createDate")).isEqualTo(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.")));
            }

            for (int i = 0; i < jsonPath.getList("recommendCounts").size(); i++) {
                String code = jsonPath.getString("recommendCounts[" + i + "].type");
                assertThat(jsonPath.getLong("recommendCounts[" + i + "].count")).isEqualTo(counting.get(EnumCodeConverterUtils.ofCode(code, RecommendType.class)));
            }
        }

        @Test
        void 다음_리뷰_리스트를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .param("moreId", 10L)
                    .when().get("/api/review/store/{storeId}", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            LocalDate now = LocalDate.now();
            Map<RecommendType, Long> counting = reviewRecommendRepository.findAll().stream()
                    .collect(Collectors.groupingBy(ReviewRecommend::getRecommendType, Collectors.counting()));

            // then
            assertThat(jsonPath.getList("reviews")).hasSameSizeAs(store.getReviews());
            for (int i = 0; i < jsonPath.getList("reviews").size(); i++) {
                assertThat(jsonPath.getLong("reviews[" + i + "].reviewId")).isEqualTo(store.getReviews().get(i).getId());
                assertThat(jsonPath.getString("reviews[" + i + "].nickname")).isEqualTo(store.getReviews().get(i).getUser().getNickname());
                assertThat(jsonPath.getInt("reviews[" + i + "].starScore")).isEqualTo(store.getReviews().get(i).getStarScore());
                assertThat(jsonPath.getString("reviews[" + i + "].content")).isEqualTo(store.getReviews().get(i).getContent());
                assertThat(jsonPath.getInt("reviews[" + i + "].likeCnt")).isEqualTo(store.getReviews().get(i).getLikeCnt());
                assertThat(CollectionUtils.isEqualCollection(
                        jsonPath.getList("reviews[" + i + "].images"),
                        store.getReviews().get(i).getReviewImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList())
                )).isTrue();

                for (int j = 0; j < jsonPath.getList("reviews[" + i + "].recommends").size(); j++) {
                    System.out.println(jsonPath.getString("reviews[" + i + "].recommends[" + j + "].type") + " aaaaaaaaaaa");
                    assertThat(CollectionUtils.containsAny(
                            store.getReviews().get(i).getReviewRecommends().stream()
                                    .map(ReviewRecommend::getRecommendType)
                                    .collect(Collectors.toSet()),
                            EnumCodeConverterUtils.ofCode(jsonPath.getString("reviews[" + i + "].recommends[" + j + "].type"), RecommendType.class))
                    ).isTrue();
                }

                assertThat(jsonPath.getString("reviews[" + i + "].createDate")).isEqualTo(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.")));
            }
        }
    }

    @Nested
    class writeReview {

        private final WriteReviewRequestDto requestDto = WriteReviewRequestDto.builder()
                .starScore(5)
                .revisitYn(true)
                .content("리뷰 작성 리뷰 작성 리뷰 작성")
                .images(List.of("http://localhost:4000/temp/2023/4/14/image1.png"))
                .recommends(List.of(1, 2))
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

            createImage();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", 23829251L)
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isNotNull();
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

            closeMockWebServer();
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", 23829251L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        void 매장을_찾을_수_없으면_KakaoNotExistPlace를_발생한다() throws Exception {
            // given
            startMockWebServer();

            setPlaceWebServer();
            setMapsWebServer(false);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", 23829251L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.KAKAO_NOT_EXIST_PLACE.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.KAKAO_NOT_EXIST_PLACE.getMessage());

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

            createImage();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", 23829251L)
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isNotNull();
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

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

            createImage();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", 23829251L)
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isNotNull();
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

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
                    .when().post("/api/review/place/{placeId}", 23829251L)
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
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .build();
            store = storeRepository.save(store);

            createImage();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", store.getKakaoPlaceId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

            closeMockWebServer();
        }

        @Test
        void 리뷰가_존재하지만_이번주에_작성하지_않았다() throws Exception {
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
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .build();
            store = storeRepository.save(store);

            Review review = Review.builder()
                    .store(store)
                    .user(user)
                    .content("리뷰 작성")
                    .revisitYn(true)
                    .build();
            review = reviewRepository.saveAndFlush(review);
            review.setDateTime(LocalDateTime.now().minusDays(7), LocalDateTime.now());
            reviewRepository.saveAndFlush(review);

            createImage();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", store.getKakaoPlaceId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isNotNull();
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

            closeMockWebServer();
        }

        @Test
        void 이미_리뷰를_작성했으면_AlreadyExistReviewException를_발생한다() throws Exception {
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
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .build();
            store.writeReview(Review.builder()
                    .store(store)
                    .user(user)
                    .content("리뷰 작성")
                    .revisitYn(true)
                    .build());
            store = storeRepository.save(store);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", store.getKakaoPlaceId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.ALREADY_EXIST_REVIEW.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.ALREADY_EXIST_REVIEW.getMessage());

            closeMockWebServer();
        }

        @Test
        void 메인_이미지가_등록이_안되어있고_등록하는_이미지도_없다() throws Exception {
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
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .build();
            store = storeRepository.save(store);

            WriteReviewRequestDto requestDto = WriteReviewRequestDto.builder()
                    .starScore(5)
                    .revisitYn(true)
                    .content("리뷰 작성 리뷰 작성 리뷰 작성")
                    .recommends(List.of(1, 2))
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", store.getKakaoPlaceId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

            closeMockWebServer();
        }

        @Test
        void 메인_이미지가_등록이_되어있다() throws Exception {
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
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .mainImageUrl("http://localhost:4000/image1.png")
                    .build();
            store = storeRepository.save(store);

            createImage();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", store.getKakaoPlaceId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

            closeMockWebServer();
        }

        @Test
        void 이미지가_등록이_안되있지만_이미지가_비어있다() throws Exception {
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
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(5)
                    .build();
            store = storeRepository.save(store);

            WriteReviewRequestDto requestDto = WriteReviewRequestDto.builder()
                    .starScore(5)
                    .revisitYn(true)
                    .content("리뷰 작성 리뷰 작성 리뷰 작성")
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/review/place/{placeId}", store.getKakaoPlaceId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("storeId")).isEqualTo(store.getId());
            assertThat(jsonPath.getLong("reviewId")).isNotNull();

            closeMockWebServer();
        }

        private void createImage() throws Exception {
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            File tempFile = Paths.get(UPLOAD_PATH, "/temp/2023/4/14/image1.png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();
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
        ReflectionTestUtils.setField(kakaoKeywordClient, "mapsClient", WebClient.create().mutate().baseUrl(uri).defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build());

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