package com.ojeomme.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojeomme.dto.response.review.ReviewResponseDto.RecommendResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReviewListResponseDto {

    private final List<ReviewResponseDto> reviews;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<RecommendCount> recommendCounts;

    public ReviewListResponseDto(List<ReviewResponseDto> reviews, List<RecommendCount> recommendCounts) {
        this.reviews = reviews;
        this.recommendCounts = recommendCounts;
    }

    @Getter
    @NoArgsConstructor
    public static class ReviewResponseDto {

        private Long reviewId;
        private String nickname;
        private int starScore;
        private String content;
        private int likeCnt;
        private List<String> images;
        private List<RecommendResponseDto> recommends;
        private String profile;
        private boolean isLike;
        private boolean isWrite;
        private int userReviewCnt;
        private int userLikeCnt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd.")
        private LocalDateTime createDate;

        @Builder
        public ReviewResponseDto(Long reviewId, String nickname, int starScore, String content, int likeCnt, List<String> images, List<RecommendResponseDto> recommends, String profile, boolean isLike, boolean isWrite, int userReviewCnt, int userLikeCnt, LocalDateTime createDate) {
            this.reviewId = reviewId;
            this.nickname = nickname;
            this.starScore = starScore;
            this.content = content;
            this.likeCnt = likeCnt;
            this.images = images;
            this.recommends = recommends;
            this.profile = profile;
            this.isLike = isLike;
            this.isWrite = isWrite;
            this.userReviewCnt = userReviewCnt;
            this.userLikeCnt = userLikeCnt;
            this.createDate = createDate;
        }

        public boolean getIsLike() {
            return isLike;
        }

        public boolean getIsWrite() {
            return isWrite;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public void setRecommends(List<RecommendResponseDto> recommends) {
            this.recommends = recommends;
        }
    }

    @Getter
    public static class RecommendCount {

        private final int type;
        private final String name;
        private final Long count;

        public RecommendCount(int type, String name, Long count) {
            this.type = type;
            this.name = name;
            this.count = count;
        }
    }
}
