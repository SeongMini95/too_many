package com.ojeomme.dto.response.review;

import lombok.Getter;

@Getter
public class LikeReviewResponseDto {

    private final boolean result;
    private final int likeCnt;

    public LikeReviewResponseDto(boolean result, int likeCnt) {
        this.result = result;
        this.likeCnt = likeCnt;
    }
}
