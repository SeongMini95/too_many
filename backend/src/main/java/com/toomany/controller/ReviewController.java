package com.toomany.controller;

import com.toomany.config.auth.LoginUser;
import com.toomany.dto.request.review.WriteReviewRequestDto;
import com.toomany.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Long> writeReview(@LoginUser Long userId, @Valid @RequestBody WriteReviewRequestDto requestDto) {
        Long storeId = reviewService.writeReview(userId, requestDto);
        return ResponseEntity.ok(storeId);
    }
}
