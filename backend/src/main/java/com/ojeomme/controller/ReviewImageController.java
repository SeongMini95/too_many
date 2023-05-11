package com.ojeomme.controller;


import com.ojeomme.dto.response.review.PreviewImageListResponseDto;
import com.ojeomme.dto.response.store.ReviewImageListResponseDto;
import com.ojeomme.service.ReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviewImage")
public class ReviewImageController {

    private final ReviewImageService reviewImageService;

    @GetMapping("/store/{storeId}/preview")
    public ResponseEntity<PreviewImageListResponseDto> getPreviewImageList(@PathVariable Long storeId) {
        PreviewImageListResponseDto responseDto = reviewImageService.getPreviewImageList(storeId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/store/{storeId}/list")
    public ResponseEntity<ReviewImageListResponseDto> getReviewImageList(@PathVariable Long storeId, @RequestParam(required = false) Long moreId) {
        ReviewImageListResponseDto responseDto = reviewImageService.getReviewImageList(storeId, moreId);
        return ResponseEntity.ok(responseDto);
    }
}
