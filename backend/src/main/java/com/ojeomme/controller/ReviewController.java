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
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/place/{placeId}")
    public ResponseEntity<WriteReviewResponseDto> writeReview(@LoginUser Long userId, @PathVariable Long placeId, @Valid @RequestBody WriteReviewRequestDto requestDto) throws IOException {
        WriteReviewResponseDto responseDto = reviewService.writeReview(userId, placeId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ReviewListResponseDto> getReviewList(@LoginUser Long userId, @PathVariable Long storeId, @RequestParam(required = false) Long moreId) {
        ReviewListResponseDto responseDto = reviewService.getReviewList(userId, storeId, moreId);
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

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Boolean> likeReview(@LoginUser Long userId, @PathVariable Long reviewId) {
        boolean savedYn = reviewService.likeReview(userId, reviewId);
        return ResponseEntity.ok(savedYn);
    }

    @GetMapping("/store/{storeId}/like")
    public ResponseEntity<List<Long>> getReviewLikeLogListOfStore(@LoginUser Long userId, @PathVariable Long storeId) {
        List<Long> reviewListLogList = reviewService.getReviewLikeLogListOfUser(userId, storeId);
        return ResponseEntity.ok(reviewListLogList);
    }
}
