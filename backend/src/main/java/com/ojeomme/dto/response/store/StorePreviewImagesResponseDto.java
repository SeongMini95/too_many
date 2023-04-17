package com.ojeomme.dto.response.store;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class StorePreviewImagesResponseDto {

    private final StoreResponseDto store;
    private final List<String> previewImages;

    public StorePreviewImagesResponseDto(StoreResponseDto store, List<String> previewImages) {
        this.store = store;
        this.previewImages = previewImages;
    }

    @NoArgsConstructor
    @Getter
    public static class StoreResponseDto {

        private Long storeId;
        private Long placeId;
        private String storeName;
        private String categoryName;
        private String addressName;
        private String roadAddressName;
        private int likeCnt;

        @Builder
        public StoreResponseDto(Long storeId, Long placeId, String storeName, String categoryName, String addressName, String roadAddressName, int likeCnt) {
            this.storeId = storeId;
            this.placeId = placeId;
            this.storeName = storeName;
            this.categoryName = categoryName;
            this.addressName = addressName;
            this.roadAddressName = roadAddressName;
            this.likeCnt = likeCnt;
        }
    }
}
