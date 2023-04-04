package com.toomany.service;

import com.toomany.common.maps.client.KakaoMapsClient;
import com.toomany.common.maps.entity.PlaceList;
import com.toomany.domain.review.Review;
import com.toomany.domain.store.Store;
import com.toomany.domain.store.repository.StoreRepository;
import com.toomany.dto.request.store.SearchStoreRequestDto;
import com.toomany.dto.response.store.SearchStoreListResponseDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private KakaoMapsClient kakaoMapsClient;

    @Mock
    private StoreRepository storeRepository;

    @Nested
    class searchStoreList {

        private final PlaceList placeList = PlaceList.builder()
                .meta(PlaceList.Meta.builder()
                        .pageableCount(1)
                        .isEnd(true)
                        .totalCount(1)
                        .build())
                .documents(List.of(
                        PlaceList.Document.builder()
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

        private final SearchStoreRequestDto requestDto = SearchStoreRequestDto.builder()
                .query("스시소라")
                .x("")
                .y("")
                .page(1)
                .build();

        @Test
        void 매장_목록을_가져온다() {
            // given
            given(kakaoMapsClient.getPlace(any(SearchStoreRequestDto.class))).willReturn(placeList);

            List<Store> stores = new ArrayList<>();
            stores.add(Store.builder()
                    .kakaoPlaceId(23829251L)
                    .likeCnt(5)
                    .build());
            stores.get(0).writeReview(Review.builder().build());
            given(storeRepository.findAllByKakaoPlaceIdIn(anyList())).willReturn(stores);

            // when
            SearchStoreListResponseDto responseDto = storeService.searchStoreList(requestDto);

            // then
            assertThat(responseDto.getMeta().getPageableCount()).isEqualTo(placeList.getMeta().getPageableCount());
            assertThat(responseDto.getMeta().getTotalCount()).isEqualTo(placeList.getMeta().getTotalCount());
            assertThat(responseDto.getMeta().getIsEnd()).isEqualTo(placeList.getMeta().isEnd());

            assertThat(responseDto.getStores().size()).isEqualTo(placeList.getDocuments().size());
            for (int i = 0; i < responseDto.getStores().size(); i++) {
                assertThat(responseDto.getStores().get(i).getLikeCnt()).isEqualTo(stores.get(i).getLikeCnt());
                assertThat(responseDto.getStores().get(i).getReviewCnt()).isEqualTo(stores.get(i).getReviews().size());
            }
        }

        @Test
        void 매장_목록을_가져온다_없는_매장() {
            // given
            given(kakaoMapsClient.getPlace(any(SearchStoreRequestDto.class))).willReturn(placeList);
            given(storeRepository.findAllByKakaoPlaceIdIn(anyList())).willReturn(Collections.emptyList());

            // when
            SearchStoreListResponseDto responseDto = storeService.searchStoreList(requestDto);

            // then
            assertThat(responseDto.getMeta().getPageableCount()).isEqualTo(placeList.getMeta().getPageableCount());
            assertThat(responseDto.getMeta().getTotalCount()).isEqualTo(placeList.getMeta().getTotalCount());
            assertThat(responseDto.getMeta().getIsEnd()).isEqualTo(placeList.getMeta().isEnd());

            assertThat(responseDto.getStores().size()).isEqualTo(placeList.getDocuments().size());
            for (int i = 0; i < responseDto.getStores().size(); i++) {
                assertThat(responseDto.getStores().get(i).getLikeCnt()).isEqualTo(0);
                assertThat(responseDto.getStores().get(i).getReviewCnt()).isEqualTo(0);
            }
        }
    }
}