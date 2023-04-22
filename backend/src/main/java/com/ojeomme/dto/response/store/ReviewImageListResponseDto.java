package com.ojeomme.dto.response.store;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewImageListResponseDto {

    private final List<String> images;
    private final Long moreId;

    public ReviewImageListResponseDto(List<String> images, Long moreId) {
        this.images = images;
        this.moreId = moreId;
    }
}
