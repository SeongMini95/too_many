package com.ojeomme.controller;

import com.ojeomme.config.auth.LoginUser;
import com.ojeomme.dto.request.review.WriteReviewRequestDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{placeId}")
    public ResponseEntity<Long> writeReview(@LoginUser Long userId, @PathVariable Long placeId, @Valid @RequestBody WriteReviewRequestDto requestDto) throws IOException {
        Long storeId = reviewService.writeReview(userId, placeId, requestDto);
        return ResponseEntity.ok(storeId);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ReviewListResponseDto> getReviewList(@PathVariable Long storeId, @RequestParam(required = false) Long reviewId) {
        ReviewListResponseDto responseDto = reviewService.getReviewList(storeId, reviewId);
        return ResponseEntity.ok(responseDto);
    }
}
