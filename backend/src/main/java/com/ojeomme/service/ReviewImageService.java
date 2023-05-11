package com.ojeomme.service;

import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.dto.response.review.PreviewImageListResponseDto;
import com.ojeomme.dto.response.store.ReviewImageListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewImageService {

    private final ReviewImageRepository reviewImageRepository;

    @Transactional(readOnly = true)
    public PreviewImageListResponseDto getPreviewImageList(Long storeId) {
        return reviewImageRepository.getPreviewImageList(storeId);
    }

    @Transactional(readOnly = true)
    public ReviewImageListResponseDto getReviewImageList(Long storeId, Long moreId) {
        return reviewImageRepository.getReviewImageList(storeId, moreId);
    }
}
