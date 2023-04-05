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

    private List<Document> documents = new ArrayList<>();

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

    @Builder
    public KakaoRegionCode(List<Document> documents) {
        this.documents = documents;
    }

    public String getCode() {
        return documents.stream()
                .filter(v -> v.getRegionType().equals("B"))
                .findFirst()
                .map(Document::getCode)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_EXIST_PLACE));
    }
}
