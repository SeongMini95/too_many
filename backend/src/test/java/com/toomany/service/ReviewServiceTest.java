package com.toomany.service;

import com.toomany.common.maps.client.KakaoKeywordClient;
import com.toomany.common.maps.client.KakaoPlaceClient;
import com.toomany.common.maps.client.KakaoRegionCodeClient;
import com.toomany.common.maps.entity.KakaoPlaceInfo;
import com.toomany.common.maps.entity.KakaoPlaceList;
import com.toomany.common.maps.entity.KakaoRegionCode;
import com.toomany.domain.category.Category;
import com.toomany.domain.category.repository.CategoryRepository;
import com.toomany.domain.regioncode.RegionCode;
import com.toomany.domain.regioncode.repository.RegionCodeRepository;
import com.toomany.domain.review.Review;
import com.toomany.domain.store.Store;
import com.toomany.domain.store.repository.StoreRepository;
import com.toomany.domain.user.User;
import com.toomany.domain.user.repository.UserRepository;
import com.toomany.dto.request.review.WriteReviewRequestDto;
import com.toomany.dto.request.store.SearchPlaceListRequestDto;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
                        .wpointx(508095)
                        .wpointy(1117328)
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
                .placeId(23829251L)
                .revisitYn(true)
                .content("리뷰")
                .images(List.of("image1", "image2"))
                .recommends(List.of("1", "2"))
                .x("127.03662909986537")
                .y("37.52186058560857")
                .build();

        @Test
        void 등록이_안된_store_존재하지_않는_category() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.save(any(Store.class))).willReturn(store);

            // when
            Long storeId = reviewService.writeReview(1L, requestDto);

            // then
            assertThat(storeId).isNotNull();

            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.writeReview(1L, requestDto));

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
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.writeReview(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.KAKAO_NOT_EXIST_PLACE);
        }

        @Test
        void 등록이_안된_store_존재하는_category() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            Category category = mock(Category.class);
            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.of(category));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.save(any(Store.class))).willReturn(store);

            // when
            Long storeId = reviewService.writeReview(1L, requestDto);

            // then
            assertThat(storeId).isNotNull();

            then(store).should(times(1)).writeReview(any(Review.class));
        }

        @Test
        void category가_null이지만_상위_category는_존재() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
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

            // when
            Long storeId = reviewService.writeReview(1L, requestDto);

            // then
            assertThat(storeId).isNotNull();

            then(store).should(times(1)).writeReview(any(Review.class));
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
            ApiException exception = assertThrows(ApiException.class, () -> reviewService.writeReview(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }

        @Test
        void store가_존재할_때() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.empty());
            given(kakaoPlaceClient.getKakaoPlaceInfo(anyLong())).willReturn(kakaoPlaceInfo);
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(true))).willReturn(kakaoPlaceList);

            given(categoryRepository.findByCategoryDepthAndCategoryName(anyInt(), anyString())).willReturn(Optional.empty());
            given(categoryRepository.save(any(Category.class))).willReturn(mock(Category.class));

            given(kakaoRegionCodeClient.getRegionCode(anyString(), anyString())).willReturn(kakaoRegionCode);
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(regionCode));

            Store store = mock(Store.class);
            given(storeRepository.findByKakaoPlaceId(anyLong())).willReturn(Optional.of(store));

            // when
            Long storeId = reviewService.writeReview(1L, requestDto);

            // then
            assertThat(storeId).isNotNull();

            then(store).should(times(1)).updateStoreInfo(any(Store.class));
            then(store).should(times(1)).writeReview(any(Review.class));
        }
    }
}