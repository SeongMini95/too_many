package com.ojeomme.domain.review.repository;

import com.ojeomme.domain.review.Review;
import com.ojeomme.dto.response.review.ReviewListResponseDto;

import java.util.Optional;

public interface ReviewCustomRepository {

    ReviewListResponseDto getReviewList(Long storeId, Long reviewId);

    Optional<Review> getWithinAWeek(Long userId, Long placeId);
}
