package com.ojeomme.domain.review.repository;

import com.ojeomme.domain.review.Review;
import com.ojeomme.dto.response.review.ReviewListResponseDto;

import java.util.Optional;

public interface ReviewCustomRepository {

    ReviewListResponseDto getReviewList(Long userId, Long storeId, Long moreId);

    Optional<Review> getWithinAWeek(Long userId, Long placeId);

    ReviewListResponseDto getRefreshReviewList(Long userId, Long storeId, Long lastId);
}
