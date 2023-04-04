package com.toomany.common.maps.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class PlaceList {

    private Meta meta;
    private List<Document> documents = new ArrayList<>();

    @Builder
    public PlaceList(Meta meta, List<Document> documents) {
        this.meta = meta;
        this.documents = documents;
    }

    @NoArgsConstructor
    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Meta {

        private int totalCount;
        private int pageableCount;
        private boolean isEnd;

        @Builder
        public Meta(int totalCount, int pageableCount, boolean isEnd) {
            this.totalCount = totalCount;
            this.pageableCount = pageableCount;
            this.isEnd = isEnd;
        }
    }

    @NoArgsConstructor
    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Document {

        private String id;
        private String placeName;
        private String categoryName;
        private String phone;
        private String addressName;
        private String roadAddressName;
        private String x;
        private String y;
        private String distance;

        @Builder
        public Document(String id, String placeName, String categoryName, String phone, String addressName, String roadAddressName, String x, String y, String distance) {
            this.id = id;
            this.placeName = placeName;
            this.categoryName = categoryName;
            this.phone = phone;
            this.addressName = addressName;
            this.roadAddressName = roadAddressName;
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }

    public List<Long> getKakaoPlaceIds() {
        return documents.stream()
                .map(v -> Long.parseLong(v.getId()))
                .collect(Collectors.toList());
    }
}
