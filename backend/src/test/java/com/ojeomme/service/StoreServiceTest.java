package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.SearchPlaceListResponseDto;
import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto;
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

            assertThat(responseDto.getPreviewImages().size()).isEqualTo(previewImages.size());
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
            assertThat(responseDto.getMeta().getIsEnd()).isEqualTo(kakaoPlaceList.getMeta().isEnd());

            assertThat(responseDto.getPlaces().size()).isEqualTo(kakaoPlaceList.getDocuments().size());
            for (int i = 0; i < responseDto.getPlaces().size(); i++) {
                assertThat(responseDto.getPlaces().get(i).getLikeCnt()).isEqualTo(stores.get(i).getLikeCnt());
                assertThat(responseDto.getPlaces().get(i).getReviewCnt()).isEqualTo(stores.get(i).getReviews().size());
            }
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
            assertThat(responseDto.getMeta().getIsEnd()).isEqualTo(kakaoPlaceList.getMeta().isEnd());

            assertThat(responseDto.getPlaces().size()).isEqualTo(kakaoPlaceList.getDocuments().size());
            for (int i = 0; i < responseDto.getPlaces().size(); i++) {
                assertThat(responseDto.getPlaces().get(i).getLikeCnt()).isEqualTo(0);
                assertThat(responseDto.getPlaces().get(i).getReviewCnt()).isEqualTo(0);
            }
        }
    }
}