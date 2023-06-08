package com.ojeomme.common.maps.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class KakaoAddressCoord {

    private Meta meta;
    private List<Document> documents = new ArrayList<>();

    @Builder
    public KakaoAddressCoord(Meta meta, List<Document> documents) {
        this.meta = meta;
        this.documents = documents;
    }

    @NoArgsConstructor
    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Meta {

        private int totalCount;

        @Builder
        public Meta(int totalCount) {
            this.totalCount = totalCount;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Document {

        private String x;
        private String y;

        @Builder
        public Document(String x, String y) {
            this.x = x;
            this.y = y;
        }
    }

    public boolean exist() {
        return meta.getTotalCount() > 0;
    }

    public String getX() {
        return documents.get(0).getX();
    }

    public String getY() {
        return documents.get(0).getY();
    }
}
