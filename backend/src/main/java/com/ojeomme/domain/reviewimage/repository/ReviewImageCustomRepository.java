package com.ojeomme.domain.reviewimage.repository;

import com.ojeomme.dto.response.reviewimage.PreviewImageListResponseDto;
import com.ojeomme.dto.response.reviewimage.ReviewImageListResponseDto;

public interface ReviewImageCustomRepository {

    PreviewImageListResponseDto getPreviewImageList(Long storeId);

    ReviewImageListResponseDto getReviewImageList(Long storeId, Long moreId);
}
