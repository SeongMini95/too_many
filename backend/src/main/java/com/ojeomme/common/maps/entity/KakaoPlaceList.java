package com.ojeomme.common.maps.entity;

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
public class KakaoPlaceList {

    private Meta meta;
    private List<Document> documents = new ArrayList<>();

    private String[] categoryNames;

    @Builder
    public KakaoPlaceList(Meta meta, List<Document> documents, String[] categoryNames) {
        this.meta = meta;
        this.documents = documents;
        this.categoryNames = categoryNames;
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

        public boolean getIsEnd() {
            return this.isEnd;
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

    public boolean exist() {
        return meta.getTotalCount() == 1;
    }

    public int getDepth() {
        setCategoryNames();
        return categoryNames.length - 1;
    }

    public String getLastCategoryName() {
        setCategoryNames();
        return categoryNames[categoryNames.length - 1];
    }

    public String getX() {
        return documents.get(0).getX();
    }

    public String getY() {
        return documents.get(0).getY();
    }

    private void setCategoryNames() {
        if (categoryNames == null) {
            categoryNames = documents.get(0).getCategoryName().split(" > ");
        }
    }
}
