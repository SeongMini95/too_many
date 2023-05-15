package com.ojeomme.dto.response.review;

import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewimage.ReviewImage;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReviewResponseDto {

    private final Long reviewId;
    private final int starScore;
    private final String content;
    private final boolean revisitYn;
    private final List<String> images;
    private final List<RecommendResponseDto> recommends;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getId();
        this.starScore = review.getStarScore();
        this.content = review.getContent();
        this.revisitYn = review.isRevisitYn();

        this.images = review.getReviewImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());

        this.recommends = review.getReviewRecommends().stream()
                .map(v -> new RecommendResponseDto(
                        Integer.parseInt(v.getRecommendType().getCode()),
                        v.getRecommendType().getDesc()
                ))
                .collect(Collectors.toList());
    }

    @Getter
    public static class RecommendResponseDto {

        private final int type;
        private final String name;

        public RecommendResponseDto(int type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
