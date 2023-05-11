package com.ojeomme.dto.response.review;

import lombok.Getter;

@Getter
public class WriteReviewResponseDto {

    private final Long storeId;
    private final Long reviewId;

    public WriteReviewResponseDto(Long storeId, Long reviewId) {
        this.storeId = storeId;
        this.reviewId = reviewId;
    }
}
