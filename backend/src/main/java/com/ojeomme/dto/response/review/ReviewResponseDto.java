package com.ojeomme.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewimage.ReviewImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ReviewResponseDto {

    private Long reviewId;
    private Long userId;
    private String nickname;
    private int starScore;
    private String content;
    private boolean revisitYn;
    private Set<String> images;
    private Set<String> recommends;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일")
    private LocalDateTime createDate;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getId();
        this.userId = review.getUser().getId();
        this.nickname = review.getUser().getNickname();
        this.starScore = review.getStarScore();
        this.content = review.getContent();
        this.revisitYn = review.isRevisitYn();
        this.images = review.getReviewImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toCollection(LinkedHashSet::new));
        this.recommends = review.getReviewRecommends().stream().map(v -> v.getRecommendType().getCode()).collect(Collectors.toCollection(LinkedHashSet::new));
        this.createDate = review.getCreateDatetime();
    }

    @Builder
    public ReviewResponseDto(Long reviewId, Long userId, String nickname, int starScore, String content, boolean revisitYn, Set<String> images, Set<String> recommends, LocalDateTime createDate) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.nickname = nickname;
        this.starScore = starScore;
        this.content = content;
        this.revisitYn = revisitYn;
        this.images = images;
        this.recommends = recommends;
        this.createDate = createDate;
    }
}
