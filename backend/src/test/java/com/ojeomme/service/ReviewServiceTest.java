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
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.reviewlikelog.ReviewLikeLog;
import com.ojeomme.domain.reviewlikelog.ReviewLikeLogId;
import com.ojeomme.domain.reviewlikelog.repository.ReviewLikeLogRepository;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.review.ModifyReviewRequestDto;
import com.ojeomme.dto.request.review.WriteReviewRequestDto;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.dto.response.review.ReviewResponseDto;
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

    @Mock
    private ReviewLikeLogRepository reviewLikeLogRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Nested
    class getReviewLikeLogListOfUser {

        @Test
        void 유저의_해당_매장의_리뷰_좋아요_목록을_가져온다() {
            // given
            List<ReviewLikeLog> reviewLikeLogs = List.of(
                    ReviewLikeLog.builder().review(Review.builder().id(1L).build()).user(User.builder().id(1L).build()).build(),
                    ReviewLikeLog.builder().review(Review.builder().id(3L).build()).user(User.builder().id(1L).build()).build(),
                    ReviewLikeLog.builder().review(Review.builder().id(5L).build()).user(User.builder().id(1L).build()).build()
            );
            given(reviewLikeLogRepository.findByUserIdAndReviewStoreId(anyLong(), anyLong())).willReturn(reviewLikeLogs);

            // when
            List<Long> reviewLikeLogList = reviewService.getReviewLikeLogListOfUser(1L, 1L);

            // then
            assertThat(reviewLikeLogList).isEqualTo(reviewLikeLogs.stream().map(v -> v.getReview().getId()).collect(Collectors.toList()));
        }
    }

    @Nested
    class likeReview {

        @Test
        void 리뷰_좋아요를_누른다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));

            Review review = mock(Review.class);
            given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
            given(review.getStore()).willReturn(mock(Store.class));

            given(reviewLikeLogRepository.findById(any(ReviewLikeLogId.class))).willReturn(Optional.empty());
            given(reviewLikeLogRepository.save(any(ReviewLikeLog.class))).willReturn(mock(ReviewLikeLog.class));
            given(reviewRepository.existsByStoreIdAndLikeCntGreaterThan(anyLong(), anyInt())).willReturn(true);

            // when
            boolean savedYn = reviewService.likeReview(1L, 1L);

            // then
            assertThat(savedYn).isTrue();
        }

        @Test
        void 리뷰가_이미_존재한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));

            Review review = mock(Review.class);
            given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));
            given(review.getStore()).willReturn(mock(Store.class));

            given(reviewLikeLogRepository.findById(any(ReviewLikeLogId.class))).willReturn(Optional.of(mock(ReviewLikeLog.class)));
            given(reviewRepository.existsByStoreIdAndLikeCntGreaterThan(anyLong(), anyInt())).willReturn(false);

            given(reviewImageRepository.findTopByReviewId(anyLong())).willReturn(Optional.of(mock(ReviewImage.class)));

            // when
            boolean savedYn = reviewService.likeReview(1L, 1L);

            // then
            assertThat(savedYn).isFalse();
        }

        @Test
        void 유저를_찾지못하면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.likeReview(1L, 1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 리뷰를_찾지_못하면_ReviewNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.likeReview(1L, 1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class deleteReview {

        @Test
        void 리뷰를_삭제한다() {
            // given
            given(reviewRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(mock(Review.class)));

            // when
            reviewService.deleteReview(1L, 1L);

            // then
            then(reviewRepository).should(times(1)).delete(any(Review.class));
        }

        @Test
        void 리뷰가_존재하지_않으면_ReviewNotFoundException를_발생한다() {
            // given
            given(reviewRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.deleteReview(1L, 1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND);
        }
    }

    @Nested
    class modifyReview {

        private final ModifyReviewRequestDto requestDto = ModifyReviewRequestDto.builder()
                .starScore(3)
                .content("리뷰 수정 테스트 입니다.")
                .revisitYn(true)
                .images(List.of(
                        "http://localhost:4000/temp/2023/4/14/image1.png",
                        "http://localhost:4000/temp/2023/4/14/image2.png"
                ))
                .recommends(List.of(1))
                .build();

        @Test
        void 리뷰를_수정한다() throws IOException {
            // given
            User user = User.builder()
                    .id(1L)
                    .nickname("test123")
                    .build();
            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(5)
                    .content("리뷰")
                    .revisitYn(false)
                    .build();
            given(reviewRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(review));

            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image1.png"))).willReturn("http://localhost:4000/2023/4/14/image1.png");
            given(imageService.copyImage(eq("http://localhost:4000/temp/2023/4/14/image2.png"))).willReturn("http://localhost:4000/2023/4/14/image2.png");

            // when
            ReviewResponseDto responseDto = reviewService.modifyReview(1L, 1L, requestDto);

            // then
            assertThat(responseDto.getReviewId()).isEqualTo(1L);
            assertThat(responseDto.getNickname()).isEqualTo(user.getNickname());
            assertThat(responseDto.getStarScore()).isEqualTo(requestDto.getStarScore());
            assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());
            assertThat(CollectionUtils.isEqualCollection(responseDto.getImages(), requestDto.getImages())).isTrue();
            assertThat(CollectionUtils.isEqualCollection(responseDto.getRecommends(), requestDto.getRecommends())).isTrue();
        }

        @Test
        void 리뷰가_없으면_ReviewNotFoundException를_발생한다() {
            // given
            given(reviewRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.modifyReview(1L, 1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REVIEW_NOT_FOUND);
        }
    }

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
                    .likeCnt(3)
                    .images(List.of("http://localhost:4000/image1.png"))
                    .recommends(List.of(1))
                    .createDate(LocalDateTime.of(2023, 4, 18, 0, 0))
                    .build();
            ReviewResponseDto reviewResponseDto2 = ReviewResponseDto.builder()
                    .reviewId(2L)
                    .nickname("nick2")
                    .starScore(5)
                    .content("리뷰2")
                    .revisitYn(false)
                    .likeCnt(5)
                    .images(List.of("http://localhost:4000/image2.png"))
                    .recommends(List.of(2))
                    .createDate(LocalDateTime.of(2023, 4, 17, 0, 0))
                    .build();
            ReviewListResponseDto reviewListResponseDto = new ReviewListResponseDto(List.of(reviewResponseDto1, reviewResponseDto2));
            given(reviewRepository.getReviewList(anyLong(), anyLong(), anyLong())).willReturn(reviewListResponseDto);

            // when
            ReviewListResponseDto responseDto = reviewService.getReviewList(1L, 1L, 1L);

            // then
            assertThat(responseDto.getReviews()).hasSameSizeAs(reviewListResponseDto.getReviews());
            for (int i = 0; i < responseDto.getReviews().size(); i++) {
                assertThat(responseDto.getReviews().get(i).getReviewId()).isEqualTo(reviewListResponseDto.getReviews().get(i).getReviewId());
                assertThat(responseDto.getReviews().get(i).getNickname()).isEqualTo(reviewListResponseDto.getReviews().get(i).getNickname());
                assertThat(responseDto.getReviews().get(i).getStarScore()).isEqualTo(reviewListResponseDto.getReviews().get(i).getStarScore());
                assertThat(responseDto.getReviews().get(i).getContent()).isEqualTo(reviewListResponseDto.getReviews().get(i).getContent());
                assertThat(responseDto.getReviews().get(i).isRevisitYn()).isEqualTo(reviewListResponseDto.getReviews().get(i).isRevisitYn());
                assertThat(responseDto.getReviews().get(i).getLikeCnt()).isEqualTo(reviewListResponseDto.getReviews().get(i).getLikeCnt());
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
                .recommends(List.of(1, 2))
                .x("127.03662909986537")
                .y("37.52186058560857")
                .build();

        @Test
        void 등록이_안된_store_존재하지_않는_category() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

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

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();
            review.addImages(requestDto.getImages().stream().map(v -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(v)
                            .build())
                    .collect(Collectors.toSet()));
            review.addRecommends(requestDto.getRecommends().stream().map(v -> ReviewRecommend.builder()
                            .review(review)
                            .recommendType(EnumCodeConverterUtils.ofCode(String.valueOf(v), RecommendType.class))
                            .build())
                    .collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();

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

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();
            review.addImages(requestDto.getImages().stream().map(v -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(v)
                            .build())
                    .collect(Collectors.toSet()));
            review.addRecommends(requestDto.getRecommends().stream().map(v -> ReviewRecommend.builder()
                            .review(review)
                            .recommendType(EnumCodeConverterUtils.ofCode(String.valueOf(v), RecommendType.class))
                            .build())
                    .collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();
        }

        @Test
        void category가_null이지만_상위_category는_존재() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

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

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();
            review.addImages(requestDto.getImages().stream().map(v -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(v)
                            .build())
                    .collect(Collectors.toSet()));
            review.addRecommends(requestDto.getRecommends().stream().map(v -> ReviewRecommend.builder()
                            .review(review)
                            .recommendType(EnumCodeConverterUtils.ofCode(String.valueOf(v), RecommendType.class))
                            .build())
                    .collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();
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

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();
            review.addImages(requestDto.getImages().stream().map(v -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(v)
                            .build())
                    .collect(Collectors.toSet()));
            review.addRecommends(requestDto.getRecommends().stream().map(v -> ReviewRecommend.builder()
                            .review(review)
                            .recommendType(EnumCodeConverterUtils.ofCode(String.valueOf(v), RecommendType.class))
                            .build())
                    .collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();

            then(store).should(times(1)).updateStoreInfo(any(Store.class));
            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void 리뷰가_존재하지만_이번주에_작성하지_않았다() throws IOException {
            // given
            Review prevReview = Review.builder().build();
            prevReview.setDateTime(LocalDateTime.now().minusDays(7), null);
            given(reviewRepository.getWithinAWeek(anyLong(), anyLong())).willReturn(Optional.of(prevReview));

            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

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

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();
            review.addImages(requestDto.getImages().stream().map(v -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(v)
                            .build())
                    .collect(Collectors.toSet()));
            review.addRecommends(requestDto.getRecommends().stream().map(v -> ReviewRecommend.builder()
                            .review(review)
                            .recommendType(EnumCodeConverterUtils.ofCode(String.valueOf(v), RecommendType.class))
                            .build())
                    .collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();

            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void 메인_이미지가_등록이_안되어있고_등록하는_이미지도_없다() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.of(store));
            given(store.getMainImageUrl()).willReturn(null);

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();
            review.addRecommends(requestDto.getRecommends().stream().map(v -> ReviewRecommend.builder()
                            .review(review)
                            .recommendType(EnumCodeConverterUtils.ofCode(String.valueOf(v), RecommendType.class))
                            .build())
                    .collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            WriteReviewRequestDto requestDto = WriteReviewRequestDto.builder()
                    .revisitYn(true)
                    .starScore(5)
                    .content("리뷰")
                    .recommends(List.of(1, 2))
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .build();

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();

            then(store).should(times(1)).updateStoreInfo(any(Store.class));
            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void 메인_이미지가_등록이_되어있다() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.of(store));
            given(store.getMainImageUrl()).willReturn("image1");

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();
            review.addRecommends(requestDto.getRecommends().stream().map(v -> ReviewRecommend.builder()
                            .review(review)
                            .recommendType(EnumCodeConverterUtils.ofCode(String.valueOf(v), RecommendType.class))
                            .build())
                    .collect(Collectors.toSet()));

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();

            then(store).should(times(1)).updateStoreInfo(any(Store.class));
            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void 이미지가_등록이_안되있지만_이미지가_비어있다() throws IOException {
            // given
            User user = mock(User.class);
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.of(store));
            given(store.getMainImageUrl()).willReturn(null);

            Review review = Review.builder()
                    .id(1L)
                    .user(user)
                    .starScore(requestDto.getStarScore())
                    .content(requestDto.getContent())
                    .revisitYn(requestDto.isRevisitYn())
                    .build();

            given(reviewRepository.save(any(Review.class))).willReturn(review);

            WriteReviewRequestDto requestDto = WriteReviewRequestDto.builder()
                    .revisitYn(true)
                    .starScore(5)
                    .content("리뷰")
                    .x("127.03662909986537")
                    .y("37.52186058560857")
                    .build();

            // when
            WriteReviewResponseDto responseDto = reviewService.writeReview(1L, 23829251L, requestDto);

            // then
            assertThat(responseDto.getStoreId()).isNotNull();
            assertThat(responseDto.getReviewId()).isNotNull();

            then(store).should(times(1)).updateStoreInfo(any(Store.class));
            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void 이미_리뷰를_작성했으면_AlreadyExistReviewException를_발생한다() {
            // given
            Review review = Review.builder().build();
            review.setDateTime(LocalDateTime.now(), null);
            given(reviewRepository.getWithinAWeek(anyLong(), anyLong())).willReturn(Optional.of(review));

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.writeReview(1L, 23829251L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.ALREADY_EXIST_REVIEW);
        }
    }
}