package com.ojeomme.controller;

import com.ojeomme.config.auth.LoginUser;
import com.ojeomme.dto.request.review.ModifyReviewRequestDto;
import com.ojeomme.dto.request.review.WriteReviewRequestDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.dto.response.review.ReviewResponseDto;
import com.ojeomme.dto.response.review.WriteReviewResponseDto;
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
    public ResponseEntity<WriteReviewResponseDto> writeReview(@LoginUser Long userId, @PathVariable Long placeId, @Valid @RequestBody WriteReviewRequestDto requestDto) throws IOException {
        WriteReviewResponseDto responseDto = reviewService.writeReview(userId, placeId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ReviewListResponseDto> getReviewList(@PathVariable Long storeId, @RequestParam(required = false) Long reviewId) {
        ReviewListResponseDto responseDto = reviewService.getReviewList(storeId, reviewId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> modifyReview(@LoginUser Long userId, @PathVariable Long reviewId, @Valid @RequestBody ModifyReviewRequestDto requestDto) throws IOException {
        ReviewResponseDto responseDto = reviewService.modifyReview(userId, reviewId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@LoginUser Long userId, @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok().build();
    }
}
