package com.toomany.common.maps.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class KakaoRegionCode {

    private Meta meta;
    private List<Document> documents = new ArrayList<>();

    @Builder
    public KakaoRegionCode(Meta meta, List<Document> documents) {
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
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Document {

        private String regionType;
        private String code;

        @Builder
        public Document(String regionType, String code) {
            this.regionType = regionType;
            this.code = code;
        }
    }

    public boolean exist() {
        return meta.getTotalCount() > 0 && documents.stream().anyMatch(v -> v.getRegionType().equals("B"));
    }

    public String getCode() {
        return documents.stream()
                .filter(v -> v.getRegionType().equals("B"))
                .findFirst()
                .map(Document::getCode)
                .orElseThrow(() -> new ApiException(ApiErrorCode.KAKAO_SEARCH_REGION_CODE));
    }
}
