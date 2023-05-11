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

    @Builder
    public StoreResponseDto(Long storeId, Long placeId, String storeName, String categoryName, String regionName, String addressName, String roadAddressName, String x, String y, int likeCnt) {
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
    }
}
