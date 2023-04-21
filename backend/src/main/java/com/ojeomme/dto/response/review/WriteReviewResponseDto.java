package com.ojeomme.dto.response.review;

import com.ojeomme.domain.review.Review;
import lombok.Getter;

@Getter
public class WriteReviewResponseDto {

    private final Long storeId;
    private final ReviewResponseDto review;

    public WriteReviewResponseDto(Long storeId, Review review) {
        this.storeId = storeId;
        this.review = new ReviewResponseDto(review);
    }
}
