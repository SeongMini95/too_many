package com.ojeomme.service;

import com.ojeomme.common.enums.EnumCodeConverterUtils;
import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.client.KakaoPlaceClient;
import com.ojeomme.common.maps.client.KakaoRegionCodeClient;
import com.ojeomme.common.maps.entity.KakaoPlaceInfo;
import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.common.maps.entity.KakaoRegionCode;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.review.repository.ReviewRepository;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.review.WriteReviewRequestDto;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto.ReviewResponseDto;
import com.ojeomme.dto.response.review.WriteReviewResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RegionCodeRepository regionCodeRepository;

    @Mock
    private KakaoPlaceClient kakaoPlaceClient;

    @Mock
    private KakaoKeywordClient kakaoKeywordClient;

    @Mock
    private KakaoRegionCodeClient kakaoRegionCodeClient;

    @Mock
    private ImageService imageService;

    @Nested
    class getReviewList {

        @Test
        void 리뷰_리스트를_가져온다() {
            // given
            ReviewResponseDto reviewResponseDto1 = ReviewResponseDto.builder()
                    .reviewId(1L)
                    .nickname("nick1")
                    .starScore(4)
                    .content("리뷰1")
                    .revisitYn(false)
                    .images(Set.of("http://localhost:4000/image1.png"))
                    .recommends(Set.of("1"))
                    .createDate(LocalDateTime.of(2023, 4, 18, 0, 0))
                    .build();
            ReviewResponseDto reviewResponseDto2 = ReviewResponseDto.builder()
                    .reviewId(2L)
                    .nickname("nick2")
                    .starScore(5)
                    .content("리뷰2")
                    .revisitYn(false)
                    .images(Set.of("http://localhost:4000/image2.png"))
                    .recommends(Set.of("2"))
                    .createDate(LocalDateTime.of(2023, 4, 17, 0, 0))
                    .build();
            ReviewListResponseDto reviewListResponseDto = new ReviewListResponseDto(List.of(reviewResponseDto1, reviewResponseDto2));
            given(reviewRepository.getReviewList(anyLong(), anyLong())).willReturn(reviewListResponseDto);

            // when
            ReviewListResponseDto responseDto = reviewService.getReviewList(1L, 1L);

            // then
            assertThat(responseDto.getReviews().size()).isEqualTo(reviewListResponseDto.getReviews().size());
            for (int i = 0; i < responseDto.getReviews().size(); i++) {
                assertThat(responseDto.getReviews().get(i).getReviewId()).isEqualTo(reviewListResponseDto.getReviews().get(i).getReviewId());
                assertThat(responseDto.getReviews().get(i).getNickname()).isEqualTo(reviewListResponseDto.getReviews().get(i).getNickname());
                assertThat(responseDto.getReviews().get(i).getStarScore()).isEqualTo(reviewListResponseDto.getReviews().get(i).getStarScore());
                assertThat(responseDto.getReviews().get(i).getContent()).isEqualTo(reviewListResponseDto.getReviews().get(i).getContent());
                assertThat(responseDto.getReviews().get(i).getImages()).isEqualTo(reviewListResponseDto.getReviews().get(i).getImages());
                assertThat(responseDto.getReviews().get(i).getRecommends()).isEqualTo(reviewListResponseDto.getReviews().get(i).getRecommends());
                assertThat(responseDto.getReviews().get(i).getCreateDate()).isEqualTo(reviewListResponseDto.getReviews().get(i).getCreateDate());
            }
        }
    }


    @Nested
    class writeReview {

        private final KakaoPlaceInfo kakaoPlaceInfo = KakaoPlaceInfo.builder()
                .isExist(true)
                .basicInfo(KakaoPlaceInfo.BasicInfo.builder()
                        .cid(23829251L)
                        .address(KakaoPlaceInfo.BasicInfo.Address.builder()
                                .addrbunho("92")
                                .addrdetail("어넥스 B동 3층")
                                .newaddr(KakaoPlaceInfo.BasicInfo.Address.Newaddr.builder()
                                        .newaddrfull("도산대로 318")
                                        .build())
                                .region(KakaoPlaceInfo.BasicInfo.Address.Region.builder()
                                        .fullname("서울 강남구 논현동")
                                        .newaddrfullname("서울 강남구")
                                        .build())
                                .build())
                        .placenamefull("스시코우지")
                        .build())
                .build();

        private final KakaoPlaceList kakaoPlaceList = KakaoPlaceList.builder()
                .meta(KakaoPlaceList.Meta.builder()
                        .isEnd(true)
                        .pageableCount(1)
                        .totalCount(1)
                        .build())
                .documents(List.of(
                        KakaoPlaceList.Document.builder()
                                .id("23829251")
                                .placeName("스시코우지")
                                .categoryName("음식점 > 일식 > 초밥,롤")
                                .phone("02-541-6200")
                                .addressName("서울 강남구 논현동 92")
                                .roadAddressName("서울 강남구 도산대로 318")
                                .x("127.03662909986537")
                                .y("37.52186058560857")
                                .distance("")
                                .build()
                ))
                .build();

        private final KakaoRegionCode kakaoRegionCode = KakaoRegionCode.builder()
                .documents(List.of(
                        KakaoRegionCode.Document.builder().regionType("B").code("1168010800").build(),
                        KakaoRegionCode.Document.builder().regionType("H").code("1168053100").build()
                ))
                .build();

        private final RegionCode regionCode = RegionCode.builder()
                .code("1168010800")
                .regionName("논현동")
                .regionDepth(3)
                .build();

        private final WriteReviewRequestDto requestDto = WriteReviewRequestDto.builder()
                .revisitYn(true)
                .starScore(5)
                .content("리뷰")
                .images(List.of("http://localhost:4000/temp/2023/4/14/image1.png", "http://localhost:4000/temp/2023/4/14/image2.png"))
                .recommends(List.of("1", "2"))
                .x("127.03662909986537")
                .y("37.52186058560857")
                .build();

        @Test
        void 등록이_안된_store_존재하지_않는_category() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(user.getId()).willReturn(1L);
            given(user.getNickname()).willReturn("test123");

            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.save(any(Store.class))).willReturn(store);

            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image1.png"))).willReturn("http://localhost:4000/2023/4/14/image1.png");
            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image2.png"))).willReturn("http://localhost:4000/2023/4/14/image2.png");

            Review review = mock(Review.class);
            given(review.getId()).willReturn(1L);
            given(review.getUser()).willReturn(user);
            given(review.getStarScore()).willReturn(requestDto.getStarScore());
            given(review.getContent()).willReturn(requestDto.getContent());
            given(review.isRevisitYn()).willReturn(requestDto.isRevisitYn());
            given(review.getReviewImages()).willReturn(requestDto.getImages().stream().map(v -> new ReviewImage(review, v)).collect(Collectors.toSet()));
            given(review.getReviewRecommends()).willReturn(requestDto.getRecommends().stream().map(v -> new ReviewRecommend(review, EnumCodeConverterUtils.ofCode(v, RecommendType.class))).collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();

            assertThat(responseDto.getReview().getUserId()).isEqualTo(1L);
            assertThat(responseDto.getReview().getNickname()).isEqualTo("test123");
            assertThat(responseDto.getReview().getContent()).isEqualTo(requestDto.getContent());
            assertThat(responseDto.getReview().getStarScore()).isEqualTo(requestDto.getStarScore());
            assertThat(responseDto.getReview().isRevisitYn()).isEqualTo(requestDto.isRevisitYn());
            assertThat(responseDto.getReview().getImages().size()).isEqualTo(requestDto.getImages().size());
            assertThat(CollectionUtils.isEqualCollection(responseDto.getReview().getRecommends(), requestDto.getRecommends())).isTrue();

            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.writeReview(1L, 23829251L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 매장을_찾을_수_없으면_KakaoNotExistPlace를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);

            KakaoPlaceList kakaoPlaceList = KakaoPlaceList.builder()
                    .meta(KakaoPlaceList.Meta.builder()
                            .totalCount(0)
                            .build())
                    .build();
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.writeReview(1L, 23829251L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_NOT_EXIST_PLACE);
        }

        @Test
        void 등록이_안된_store_존재하는_category() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(user.getId()).willReturn(1L);
            given(user.getNickname()).willReturn("test123");

            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            Category category = mock(Category.class);
            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.of(category));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.save(any(Store.class))).willReturn(store);

            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image1.png"))).willReturn("http://localhost:4000/2023/4/14/image1.png");
            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image2.png"))).willReturn("http://localhost:4000/2023/4/14/image2.png");

            Review review = mock(Review.class);
            given(review.getId()).willReturn(1L);
            given(review.getUser()).willReturn(user);
            given(review.getStarScore()).willReturn(requestDto.getStarScore());
            given(review.getContent()).willReturn(requestDto.getContent());
            given(review.isRevisitYn()).willReturn(requestDto.isRevisitYn());
            given(review.getReviewImages()).willReturn(requestDto.getImages().stream().map(v -> new ReviewImage(review, v)).collect(Collectors.toSet()));
            given(review.getReviewRecommends()).willReturn(requestDto.getRecommends().stream().map(v -> new ReviewRecommend(review, EnumCodeConverterUtils.ofCode(v, RecommendType.class))).collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();

            assertThat(responseDto.getReview().getUserId()).isEqualTo(1L);
            assertThat(responseDto.getReview().getNickname()).isEqualTo("test123");
            assertThat(responseDto.getReview().getContent()).isEqualTo(requestDto.getContent());
            assertThat(responseDto.getReview().getStarScore()).isEqualTo(requestDto.getStarScore());
            assertThat(responseDto.getReview().isRevisitYn()).isEqualTo(requestDto.isRevisitYn());
            assertThat(responseDto.getReview().getImages().size()).isEqualTo(requestDto.getImages().size());
            assertThat(CollectionUtils.isEqualCollection(responseDto.getReview().getRecommends(), requestDto.getRecommends())).isTrue();
        }

        @Test
        void category가_null이지만_상위_category는_존재() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(user.getId()).willReturn(1L);
            given(user.getNickname()).willReturn("test123");

            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.findByCategoryDepthAndCategoryName(eq(1), anyString())).willReturn(Optional.of(mock(Category.class)));
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.save(any(Store.class))).willReturn(store);

            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image1.png"))).willReturn("http://localhost:4000/2023/4/14/image1.png");
            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image2.png"))).willReturn("http://localhost:4000/2023/4/14/image2.png");

            Review review = mock(Review.class);
            given(review.getId()).willReturn(1L);
            given(review.getUser()).willReturn(user);
            given(review.getStarScore()).willReturn(requestDto.getStarScore());
            given(review.getContent()).willReturn(requestDto.getContent());
            given(review.isRevisitYn()).willReturn(requestDto.isRevisitYn());
            given(review.getReviewImages()).willReturn(requestDto.getImages().stream().map(v -> new ReviewImage(review, v)).collect(Collectors.toSet()));
            given(review.getReviewRecommends()).willReturn(requestDto.getRecommends().stream().map(v -> new ReviewRecommend(review, EnumCodeConverterUtils.ofCode(v, RecommendType.class))).collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();

            assertThat(responseDto.getReview().getUserId()).isEqualTo(1L);
            assertThat(responseDto.getReview().getNickname()).isEqualTo("test123");
            assertThat(responseDto.getReview().getContent()).isEqualTo(requestDto.getContent());
            assertThat(responseDto.getReview().getStarScore()).isEqualTo(requestDto.getStarScore());
            assertThat(responseDto.getReview().isRevisitYn()).isEqualTo(requestDto.isRevisitYn());
            assertThat(responseDto.getReview().getImages().size()).isEqualTo(requestDto.getImages().size());
            assertThat(CollectionUtils.isEqualCollection(responseDto.getReview().getRecommends(), requestDto.getRecommends())).isTrue();
        }

        @Test
        void region_code가_없으면_RegionNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.writeReview(1L, 23829251L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }

        @Test
        void store가_존재할_때() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
            given(user.getId()).willReturn(1L);
            given(user.getNickname()).willReturn("test123");

            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.of(store));

            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image1.png"))).willReturn("http://localhost:4000/2023/4/14/image1.png");
            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image2.png"))).willReturn("http://localhost:4000/2023/4/14/image2.png");

            Review review = mock(Review.class);
            given(review.getId()).willReturn(1L);
            given(review.getUser()).willReturn(user);
            given(review.getStarScore()).willReturn(requestDto.getStarScore());
            given(review.getContent()).willReturn(requestDto.getContent());
            given(review.isRevisitYn()).willReturn(requestDto.isRevisitYn());
            given(review.getReviewImages()).willReturn(requestDto.getImages().stream().map(v -> new ReviewImage(review, v)).collect(Collectors.toSet()));
            given(review.getReviewRecommends()).willReturn(requestDto.getRecommends().stream().map(v -> new ReviewRecommend(review, EnumCodeConverterUtils.ofCode(v, RecommendType.class))).collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();

            assertThat(responseDto.getReview().getUserId()).isEqualTo(1L);
            assertThat(responseDto.getReview().getNickname()).isEqualTo("test123");
            assertThat(responseDto.getReview().getContent()).isEqualTo(requestDto.getContent());
            assertThat(responseDto.getReview().getStarScore()).isEqualTo(requestDto.getStarScore());
            assertThat(responseDto.getReview().isRevisitYn()).isEqualTo(requestDto.isRevisitYn());
            assertThat(responseDto.getReview().getImages().size()).isEqualTo(requestDto.getImages().size());
            assertThat(CollectionUtils.isEqualCollection(responseDto.getReview().getRecommends(), requestDto.getRecommends())).isTrue();

            then(store).should(times(1)).updateStoreInfo(any(Store.class));
            then(store).should(times(1)).writeReview(any(Review.class));
        }
    }
}