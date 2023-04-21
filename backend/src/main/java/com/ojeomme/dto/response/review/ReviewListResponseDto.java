package com.ojeomme.dto.response.review;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewListResponseDto {

    private final List<ReviewResponseDto> reviews;

    public ReviewListResponseDto(List<ReviewResponseDto> reviews) {
        this.reviews = reviews;
    }
}
