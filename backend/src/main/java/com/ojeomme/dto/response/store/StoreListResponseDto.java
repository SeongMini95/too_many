package com.ojeomme.dto.response.store;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class StoreListResponseDto {

    private final List<StoreResponseDto> stores;
    private final int page;
    private final boolean isEnd;

    public StoreListResponseDto(List<StoreResponseDto> stores, int page, boolean isEnd) {
        this.stores = stores;
        this.page = page;
        this.isEnd = isEnd;
    }

    public boolean getIsEnd() {
        return this.isEnd;
    }

    @NoArgsConstructor
    @Getter
    public static class StoreResponseDto {

        private Long storeId;
        private String storeName;
        private String image;
        private double starScore;
        private String regionName;
        private String categoryName;
        private int likeCnt;
        private long reviewCnt;

        @Builder
        public StoreResponseDto(Long storeId, String storeName, String image, double starScore, String regionName, String categoryName, int likeCnt, long reviewCnt) {
            this.storeId = storeId;
            this.storeName = storeName;
            this.image = image;
            this.starScore = starScore;
            this.regionName = regionName;
            this.categoryName = categoryName;
            this.likeCnt = likeCnt;
            this.reviewCnt = reviewCnt;
        }
    }
}
