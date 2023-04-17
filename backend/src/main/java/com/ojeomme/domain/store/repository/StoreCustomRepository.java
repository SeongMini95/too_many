package com.ojeomme.domain.store.repository;

import com.ojeomme.dto.response.store.StoreReviewsResponseDto;

import java.util.Optional;

public interface StoreCustomRepository {

    Optional<StoreReviewsResponseDto> getStoreReview(Long storeId);
}
