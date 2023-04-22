package com.ojeomme.domain.reviewimage.repository;

import com.ojeomme.dto.response.store.ReviewImageListResponseDto;

public interface ReviewImageCustomRepository {

    ReviewImageListResponseDto getReviewImageList(Long storeId, Long reviewImageId);
}
