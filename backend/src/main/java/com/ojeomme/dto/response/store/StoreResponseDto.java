package com.ojeomme.dto.response.store;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StoreResponseDto {

    private Long storeId;
    private Long placeId;
    private String storeName;
    private String categoryName;
    private String regionName;
    private String addressName;
    private String roadAddressName;
    private String x;
    private String y;
    private int likeCnt;
    private long reviewCnt;
    private double avgStarScore;
    private boolean isLike;

    @Builder
    public StoreResponseDto(Long storeId, Long placeId, String storeName, String categoryName, String regionName, String addressName, String roadAddressName, String x, String y, int likeCnt, long reviewCnt, double avgStarScore, boolean isLike) {
        this.storeId = storeId;
        this.placeId = placeId;
        this.storeName = storeName;
        this.categoryName = categoryName;
        this.regionName = regionName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.x = x;
        this.y = y;
        this.likeCnt = likeCnt;
        this.reviewCnt = reviewCnt;
        this.avgStarScore = avgStarScore;
        this.isLike = isLike;
    }

    public boolean getIsLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}
