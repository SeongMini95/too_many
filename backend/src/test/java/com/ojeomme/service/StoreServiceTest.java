package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.storelikelog.StoreLikeLog;
import com.ojeomme.domain.storelikelog.StoreLikeLogId;
import com.ojeomme.domain.storelikelog.repository.StoreLikeLogRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.*;
import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto.StoreResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private KakaoKeywordClient kakaoKeywordClient;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreLikeLogRepository storeLikeLogRepository;

    @Nested
    class getStoreList {

        @Test
        void 매장_목록을_가져온다() {
            // given
            StoreListResponseDto dto = new StoreListResponseDto(List.of(
                    StoreListResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("매장1")
                            .image("image1")
                            .likeCnt(5)
                            .regionName("지역1")
                            .reviewCnt(10)
                            .starScore(5.0F)
                            .categoryName("일식")
                            .build(),
                    StoreListResponseDto.StoreResponseDto.builder()
                            .storeId(2L)
                            .storeName("매장2")
                            .image("image2")
                            .likeCnt(3)
                            .regionName("지역2")
                            .reviewCnt(3)
                            .starScore(4.0F)
                            .categoryName("일식")
                            .build()
            ), 1, true);
            given(storeRepository.getStoreList(anyString(), anyLong(), any(Pageable.class))).willReturn(dto);

            // when
            StoreListResponseDto responseDto = storeService.getStoreList("123", 1L, null);

            // then
            assertThat(responseDto.getStores()).hasSameSizeAs(dto.getStores());
            for (int i = 0; i < responseDto.getStores().size(); i++) {
                assertThat(responseDto.getStores().get(i).getStoreId()).isEqualTo(dto.getStores().get(i).getStoreId());
                assertThat(responseDto.getStores().get(i).getStoreName()).isEqualTo(dto.getStores().get(i).getStoreName());
                assertThat(responseDto.getStores().get(i).getImage()).isEqualTo(dto.getStores().get(i).getImage());
                assertThat(responseDto.getStores().get(i).getLikeCnt()).isEqualTo(dto.getStores().get(i).getLikeCnt());
                assertThat(responseDto.getStores().get(i).getRegionName()).isEqualTo(dto.getStores().get(i).getRegionName());
                assertThat(responseDto.getStores().get(i).getCategoryName()).isEqualTo(dto.getStores().get(i).getCategoryName());
                assertThat(responseDto.getStores().get(i).getReviewCnt()).isEqualTo(dto.getStores().get(i).getReviewCnt());
                assertThat(responseDto.getStores().get(i).getStarScore()).isEqualTo(dto.getStores().get(i).getStarScore());
            }
        }

        @Test
        void 매장_목록을_가져온다_page가_0() {
            // given
            StoreListResponseDto dto = new StoreListResponseDto(List.of(
                    StoreListResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("매장1")
                            .image("image1")
                            .likeCnt(5)
                            .regionName("지역1")
                            .reviewCnt(10)
                            .starScore(5.0F)
                            .categoryName("일식")
                            .build(),
                    StoreListResponseDto.StoreResponseDto.builder()
                            .storeId(2L)
                            .storeName("매장2")
                            .image("image2")
                            .likeCnt(3)
                            .regionName("지역2")
                            .reviewCnt(3)
                            .starScore(4.0F)
                            .categoryName("일식")
                            .build()
            ), 1, true);
            given(storeRepository.getStoreList(anyString(), anyLong(), any(Pageable.class))).willReturn(dto);

            // when
            StoreListResponseDto responseDto = storeService.getStoreList("123", 1L, 0);

            // then
            assertThat(responseDto.getStores()).hasSameSizeAs(dto.getStores());
            for (int i = 0; i < responseDto.getStores().size(); i++) {
                assertThat(responseDto.getStores().get(i).getStoreId()).isEqualTo(dto.getStores().get(i).getStoreId());
                assertThat(responseDto.getStores().get(i).getStoreName()).isEqualTo(dto.getStores().get(i).getStoreName());
                assertThat(responseDto.getStores().get(i).getImage()).isEqualTo(dto.getStores().get(i).getImage());
                assertThat(responseDto.getStores().get(i).getLikeCnt()).isEqualTo(dto.getStores().get(i).getLikeCnt());
                assertThat(responseDto.getStores().get(i).getRegionName()).isEqualTo(dto.getStores().get(i).getRegionName());
                assertThat(responseDto.getStores().get(i).getCategoryName()).isEqualTo(dto.getStores().get(i).getCategoryName());
                assertThat(responseDto.getStores().get(i).getReviewCnt()).isEqualTo(dto.getStores().get(i).getReviewCnt());
                assertThat(responseDto.getStores().get(i).getStarScore()).isEqualTo(dto.getStores().get(i).getStarScore());
            }
        }

        @Test
        void 매장_목록을_가져온다_page가_1_이상() {
            // given
            StoreListResponseDto dto = new StoreListResponseDto(List.of(
                    StoreListResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("매장1")
                            .likeCnt(5)
                            .regionName("지역1")
                            .reviewCnt(10)
                            .starScore(5.0F)
                            .build(),
                    StoreListResponseDto.StoreResponseDto.builder()
                            .storeId(2L)
                            .storeName("매장2")
                            .likeCnt(3)
                            .regionName("지역2")
                            .reviewCnt(3)
                            .starScore(4.0F)
                            .build()
            ), 1, true);
            given(storeRepository.getStoreList(anyString(), anyLong(), any(Pageable.class))).willReturn(dto);

            // when
            StoreListResponseDto responseDto = storeService.getStoreList("123", 1L, 3);

            // then
            assertThat(responseDto.getStores()).hasSameSizeAs(dto.getStores());
            for (int i = 0; i < responseDto.getStores().size(); i++) {
                assertThat(responseDto.getStores().get(i).getStoreId()).isEqualTo(dto.getStores().get(i).getStoreId());
                assertThat(responseDto.getStores().get(i).getStoreName()).isEqualTo(dto.getStores().get(i).getStoreName());
                assertThat(responseDto.getStores().get(i).getLikeCnt()).isEqualTo(dto.getStores().get(i).getLikeCnt());
                assertThat(responseDto.getStores().get(i).getRegionName()).isEqualTo(dto.getStores().get(i).getRegionName());
                assertThat(responseDto.getStores().get(i).getReviewCnt()).isEqualTo(dto.getStores().get(i).getReviewCnt());
                assertThat(responseDto.getStores().get(i).getStarScore()).isEqualTo(dto.getStores().get(i).getStarScore());
            }
        }
    }

    @Nested
    class getTodayStoreRanking {

        @Test
        void 지역의_오늘의_추천_매장_가져온다() {
            // given
            RealTimeStoreRankingResponseDto dto = new RealTimeStoreRankingResponseDto(List.of(
                    RealTimeStoreRankingResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("테스트1")
                            .regionName("청담동")
                            .build(),
                    RealTimeStoreRankingResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("테스트2")
                            .regionName("청담동")
                            .build(),
                    RealTimeStoreRankingResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("테스트3")
                            .regionName("청담동")
                            .build(),
                    RealTimeStoreRankingResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("테스트4")
                            .regionName("청담동")
                            .build(),
                    RealTimeStoreRankingResponseDto.StoreResponseDto.builder()
                            .storeId(1L)
                            .storeName("테스트5")
                            .regionName("청담동")
                            .build()
            ));
            given(storeRepository.getRealTimeStoreRanking(anyString())).willReturn(dto);

            // when
            RealTimeStoreRankingResponseDto responseDto = storeService.getTodayStoreRanking("123");

            // then
            assertThat(responseDto.getStores()).hasSameSizeAs(dto.getStores());
            for (int i = 0; i < responseDto.getStores().size(); i++) {
                assertThat(responseDto.getStores().get(i).getStoreId()).isEqualTo(dto.getStores().get(i).getStoreId());
                assertThat(responseDto.getStores().get(i).getStoreName()).isEqualTo(dto.getStores().get(i).getStoreName());
                assertThat(responseDto.getStores().get(i).getRegionName()).isEqualTo(dto.getStores().get(i).getRegionName());
            }
        }
    }

    @Nested
    class getReviewImageList {

        @Test
        void 리뷰_이미지를_가져온다() {
            // given
            ReviewImageListResponseDto images = new ReviewImageListResponseDto(List.of(
                    "http://localhost:4000/1.png",
                    "http://localhost:4000/2.png",
                    "http://localhost:4000/3.png"
            ), 3L);
            given(reviewImageRepository.getReviewImageList(anyLong(), anyLong())).willReturn(images);

            // when
            ReviewImageListResponseDto responseDto = storeService.getReviewImageList(1L, 1L);

            // then
            assertThat(responseDto.getImages()).hasSameSizeAs(images.getImages());
            assertThat(responseDto.getImages()).isEqualTo(images.getImages());
        }
    }

    @Nested
    class getStoreLikeLogOfUser {

        @Test
        void 좋아요를_누른_매장이다() {
            // given
            given(storeLikeLogRepository.existsByUserIdAndStoreId(anyLong(), anyLong())).willReturn(true);

            // when
            boolean exist = storeService.getStoreLikeLogOfUser(1L, 1L);

            // then
            assertThat(exist).isTrue();
        }

        @Test
        void 안누른_매장이다() {
            // given
            given(storeLikeLogRepository.existsByUserIdAndStoreId(anyLong(), anyLong())).willReturn(false);

            // when
            boolean exist = storeService.getStoreLikeLogOfUser(1L, 1L);

            // then
            assertThat(exist).isFalse();
        }
    }

    @Nested
    class likeStore {

        @Test
        void 매장의_좋아요를_누른다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(mock(Store.class)));

            given(storeLikeLogRepository.findById(any(StoreLikeLogId.class))).willReturn(Optional.empty());

            given(storeLikeLogRepository.save(any(StoreLikeLog.class))).willReturn(mock(StoreLikeLog.class));

            // when
            boolean savedYn = storeService.likeStore(1L, 1L);

            // then
            assertThat(savedYn).isTrue();
        }

        @Test
        void 이미_누른_매장이다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(mock(Store.class)));

            given(storeLikeLogRepository.findById(any(StoreLikeLogId.class))).willReturn(Optional.of(mock(StoreLikeLog.class)));

            // when
            boolean savedYn = storeService.likeStore(1L, 1L);

            // then
            assertThat(savedYn).isFalse();
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> storeService.likeStore(1L, 1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 매장이_존재하지_않으면_StoreNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> storeService.likeStore(1L, 1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.STORE_NOT_FOUND);
        }
    }

    @Nested
    class getStore {

        @Test
        void 매장과_프리뷰_이미지를_가져온다() {
            // given
            StoreResponseDto store = StoreResponseDto.builder()
                    .storeId(1L)
                    .placeId(1L)
                    .storeName("스시코우지")
                    .categoryName("초밥,롤")
                    .addressName("서울 강남구 논현동 92")
                    .roadAddressName("서울 강남구 도산대로 318")
                    .x("127")
                    .y("34")
                    .likeCnt(10)
                    .build();
            List<String> previewImages = List.of(
                    "http://localhost:4000/image1.png",
                    "http://localhost:4000/image2.png",
                    "http://localhost:4000/image3.png",
                    "http://localhost:4000/image4.png",
                    "http://localhost:4000/image5.png"
            );

            given(storeRepository.getStore(anyLong())).willReturn(Optional.of(store));
            given(reviewImageRepository.getPreviewImageList(anyLong(), any(Pageable.class))).willReturn(previewImages);

            // when
            StorePreviewImagesResponseDto responseDto = storeService.getStore(1L);

            // then
            assertThat(responseDto.getStore().getStoreId()).isEqualTo(store.getStoreId());
            assertThat(responseDto.getStore().getPlaceId()).isEqualTo(store.getPlaceId());
            assertThat(responseDto.getStore().getStoreName()).isEqualTo(store.getStoreName());
            assertThat(responseDto.getStore().getCategoryName()).isEqualTo(store.getCategoryName());
            assertThat(responseDto.getStore().getAddressName()).isEqualTo(store.getAddressName());
            assertThat(responseDto.getStore().getRoadAddressName()).isEqualTo(store.getRoadAddressName());
            assertThat(responseDto.getStore().getX()).isEqualTo(store.getX());
            assertThat(responseDto.getStore().getY()).isEqualTo(store.getY());
            assertThat(responseDto.getStore().getLikeCnt()).isEqualTo(store.getLikeCnt());

            assertThat(responseDto.getPreviewImages()).hasSameSizeAs(previewImages);
            for (int i = 0; i < responseDto.getPreviewImages().size(); i++) {
                assertThat(responseDto.getPreviewImages().get(i)).isEqualTo(previewImages.get(i));
            }
        }

        @Test
        void 매장이_없으면_StoreNotFoundException를_발생한다() {
            // given
            given(storeRepository.getStore(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> storeService.getStore(1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.STORE_NOT_FOUND);
        }
    }

    @Nested
    class searchPlaceList {

        private final KakaoPlaceList kakaoPlaceList = KakaoPlaceList.builder()
                .meta(KakaoPlaceList.Meta.builder()
                        .pageableCount(1)
                        .isEnd(true)
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
                                .build()
                ))
                .build();

        private final SearchPlaceListRequestDto requestDto = SearchPlaceListRequestDto.builder()
                .query("스시소라")
                .x("")
                .y("")
                .page(1)
                .build();

        @Test
        void 매장_목록을_가져온다() {
            // given
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(false))).willReturn(kakaoPlaceList);

            List<Store> stores = new ArrayList<>();
            stores.add(Store.builder()
                    .id(1L)
                    .kakaoPlaceId(23829251L)
                    .likeCnt(5)
                    .build());
            given(storeRepository.findAllByKakaoPlaceIdIn(anyList())).willReturn(stores);

            // when
            SearchPlaceListResponseDto responseDto = storeService.searchPlaceList(requestDto);

            // then
            assertThat(responseDto.getMeta().getPageableCount()).isEqualTo(kakaoPlaceList.getMeta().getPageableCount());
            assertThat(responseDto.getMeta().getTotalCount()).isEqualTo(kakaoPlaceList.getMeta().getTotalCount());
            assertThat(responseDto.getMeta().getIsEnd()).isEqualTo(kakaoPlaceList.getMeta().getIsEnd());

            assertThat(responseDto.getPlaces()).hasSameSizeAs(kakaoPlaceList.getDocuments());
        }

        @Test
        void 매장_목록을_가져온다_없는_매장() {
            // given
            given(kakaoKeywordClient.getKakaoPlaceList(any(SearchPlaceListRequestDto.class), eq(false))).willReturn(kakaoPlaceList);
            given(storeRepository.findAllByKakaoPlaceIdIn(anyList())).willReturn(Collections.emptyList());

            // when
            SearchPlaceListResponseDto responseDto = storeService.searchPlaceList(requestDto);

            // then
            assertThat(responseDto.getMeta().getPageableCount()).isEqualTo(kakaoPlaceList.getMeta().getPageableCount());
            assertThat(responseDto.getMeta().getTotalCount()).isEqualTo(kakaoPlaceList.getMeta().getTotalCount());
            assertThat(responseDto.getMeta().getIsEnd()).isEqualTo(kakaoPlaceList.getMeta().getIsEnd());

            assertThat(responseDto.getPlaces()).hasSameSizeAs(kakaoPlaceList.getDocuments());
        }
    }
}