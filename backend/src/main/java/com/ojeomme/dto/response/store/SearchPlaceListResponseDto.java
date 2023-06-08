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
public class SearchPlaceListResponseDto {

    private final MetaResponseDto meta;
    private final List<PlaceResponseDto> places;

    public SearchPlaceListResponseDto(KakaoPlaceList kakaoPlaceList, List<Store> places) {
        this.meta = new MetaResponseDto(kakaoPlaceList.getMeta());

        List<Store> copyStores = new ArrayList<>(places);
        this.places = kakaoPlaceList.getDocuments().stream()
                .map(v -> {
                    Store store = copyStores.stream()
                            .filter(v2 -> v2.getKakaoPlaceId().equals(Long.parseLong(v.getId())))
                            .findFirst()
                            .orElse(null);
                    if (store != null) {
                        copyStores.remove(store);
                    }

                    return new PlaceResponseDto(v, store);
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
            this.isEnd = meta.getIsEnd();
        }

        public boolean getIsEnd() {
            return isEnd;
        }
    }

    @Getter
    public static class PlaceResponseDto {

        private final Long storeId;
        private final Long placeId;
        private final String placeName;
        private final String categoryName;
        private final String image;
        private final String phone;
        private final String addressName;
        private final String roadAddressName;
        private final String x;
        private final String y;

        public PlaceResponseDto(Document document, Store store) {
            String[] categoryNames = document.getCategoryName().split(" > ");

            this.storeId = store != null ? store.getId() : null;
            this.placeId = Long.parseLong(document.getId());
            this.placeName = document.getPlaceName();
            this.categoryName = categoryNames[categoryNames.length - 1];
            this.image = store != null ? store.getMainImageUrl() : "";
            this.phone = document.getPhone();
            this.addressName = document.getAddressName();
            this.roadAddressName = document.getRoadAddressName();
            this.x = document.getX();
            this.y = document.getY();
        }
    }
}
