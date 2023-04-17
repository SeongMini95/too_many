package com.ojeomme.domain.review.repository;

import com.ojeomme.dto.response.review.ReviewListResponseDto;

public interface ReviewCustomRepository {

    ReviewListResponseDto getReviewList(Long storeId, Long reviewId);
}
