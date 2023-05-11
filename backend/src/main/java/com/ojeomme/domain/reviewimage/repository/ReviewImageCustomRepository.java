package com.ojeomme.domain.reviewimage.repository;

import com.ojeomme.dto.response.review.PreviewImageListResponseDto;
import com.ojeomme.dto.response.store.ReviewImageListResponseDto;

public interface ReviewImageCustomRepository {

    PreviewImageListResponseDto getPreviewImageList(Long storeId);

    ReviewImageListResponseDto getReviewImageList(Long storeId, Long moreId);
}
