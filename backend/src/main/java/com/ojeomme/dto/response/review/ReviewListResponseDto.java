package com.ojeomme.dto.response.review;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
public class ReviewListResponseDto {

    private final List<ReviewResponseDto> reviews;

    public ReviewListResponseDto(List<ReviewResponseDto> reviews) {
        this.reviews = reviews;
    }

    @NoArgsConstructor
    @Getter
    public static class ReviewResponseDto {

        private Long reviewId;
        private String nickname;
        private int starScore;
        private String content;
        private boolean revisitYn;
        private Set<String> images;
        private Set<String> recommends;

        @Builder
        public ReviewResponseDto(Long reviewId, String nickname, int starScore, String content, boolean revisitYn, Set<String> images, Set<String> recommends) {
            this.reviewId = reviewId;
            this.nickname = nickname;
            this.starScore = starScore;
            this.content = content;
            this.revisitYn = revisitYn;
            this.images = images;
            this.recommends = recommends;
        }
    }
}
