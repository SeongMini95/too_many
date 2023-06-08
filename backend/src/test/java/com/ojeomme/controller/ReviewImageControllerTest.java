package com.ojeomme.controller;

import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewImageControllerTest extends AcceptanceTest {

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RegionCodeRepository regionCodeRepository;

    @Nested
    class getPreviewImageList {

        @Test
        void 리뷰_이미지_미리보기를_가져온다() {
            // given
            reviewImageRepository.saveAll(List.of(
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/temp_image1").build(),
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/temp_image2").build(),
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/temp_image3").build(),
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/temp_image4").build(),
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/temp_image5").build(),
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/temp_image6").build(),
                    ReviewImage.builder().review(review).imageUrl("http://localhost:4000/temp_image7").build()
            ));

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/reviewImage/store/{storeId}/preview", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            List<ReviewImage> images = reviewImageRepository.findAll();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("imageCnt")).isEqualTo(images.size());
            assertThat(jsonPath.getList("images")).isEqualTo(images.subList(0, 5).stream()
                    .map(ReviewImage::getImageUrl)
                    .collect(Collectors.toList()));
        }
    }

    @Nested
    class getReviewImageList {

        @Test
        void 리뷰_이미지를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/reviewImage/store/{storeId}/list", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            List<String> reviewImages = store.getReviews().stream()
                    .flatMap(v -> v.getReviewImages().stream()
                            .sorted(Comparator.comparing(ReviewImage::getId).reversed())
                            .map(ReviewImage::getImageUrl))
                    .collect(Collectors.toList());
            assertThat(jsonPath.getList("images")).isEqualTo(reviewImages);
        }

        @Test
        void 다음_리뷰_이미지를_가져온다() {
            // given
            List<ReviewImage> images = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                images.add(ReviewImage.builder()
                        .review(review)
                        .imageUrl("http://localhost:4000/temp_image" + i + ".png")
                        .build());
            }
            reviewImageRepository.saveAll(images);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .param("moreId", images.get(20).getId())
                    .when().get("/api/reviewImage/store/{storeId}/list", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            List<String> getImages = images.subList(0, 20).stream()
                    .sorted(Comparator.comparing(ReviewImage::getId).reversed())
                    .map(ReviewImage::getImageUrl)
                    .collect(Collectors.toList());

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("images")).isEqualTo(getImages);
        }

        @Test
        void 이미지가_없다() {
            // given
            Category category = categoryRepository.save(Category.builder()
                    .categoryName("초밥,롤")
                    .categoryDepth(1)
                    .build());

            Store store = Store.builder()
                    .kakaoPlaceId(123L)
                    .category(category)
                    .regionCode(regionCodeRepository.findById("1111012200").orElseThrow())
                    .storeName("테스트")
                    .addressName("서울 종로구 청진동 146")
                    .roadAddressName("서울 종로구 종로 19")
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .likeCnt(0)
                    .build();

            store = storeRepository.save(store);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/reviewImage/store/{storeId}/list", store.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("images")).isEmpty();
        }
    }
}