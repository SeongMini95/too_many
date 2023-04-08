package com.ojeomme.dto.response.store;

import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.common.maps.entity.KakaoPlaceList.Document;
import com.ojeomme.common.maps.entity.KakaoPlaceList.Meta;
import com.ojeomme.domain.store.Store;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchStoreListResponseDto {

    private final MetaResponseDto meta;
    private final List<StoreResponseDto> stores;

    public SearchStoreListResponseDto(KakaoPlaceList kakaoPlaceList, List<Store> stores) {
        this.meta = new MetaResponseDto(kakaoPlaceList.getMeta());

        List<Store> copyStores = new ArrayList<>(stores);
        this.stores = kakaoPlaceList.getDocuments().stream()
                .map(v -> {
                    Store store = copyStores.stream()
                            .filter(v2 -> v2.getKakaoPlaceId().equals(Long.parseLong(v.getId())))
                            .findFirst()
                            .orElse(null);
                    if (store != null) {
                        copyStores.remove(store);
                    }

                    return new StoreResponseDto(v, store);
                })
                .collect(Collectors.toList());
    }

    @Getter
    public static class MetaResponseDto {

        private final int totalCount;
        private final int pageableCount;
        private final boolean isEnd;

        public MetaResponseDto(Meta meta) {
            this.totalCount = meta.getTotalCount();
            this.pageableCount = meta.getPageableCount();
            this.isEnd = meta.isEnd();
        }

        public boolean getIsEnd() {
            return isEnd;
        }
    }

    @Getter
    public static class StoreResponseDto {

        private final String storeId;
        private final String placeId;
        private final String placeName;
        private final String categoryName;
        private final String phone;
        private final String addressName;
        private final String roadAddressName;
        private final String x;
        private final String y;
        private final int likeCnt;
        private final int reviewCnt;

        public StoreResponseDto(Document document, Store store) {
            String[] categoryNames = document.getCategoryName().split(" > ");

            this.storeId = store != null ? store.getId().toString() : "";
            this.placeId = document.getId();
            this.placeName = document.getPlaceName();
            this.categoryName = categoryNames[categoryNames.length - 1];
            this.phone = document.getPhone();
            this.addressName = document.getAddressName();
            this.roadAddressName = document.getRoadAddressName();
            this.x = document.getX();
            this.y = document.getY();
            this.likeCnt = store != null ? store.getLikeCnt() : 0;
            this.reviewCnt = store != null ? store.getReviews().size() : 0;
        }
    }
}
