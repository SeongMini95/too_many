package com.ojeomme.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewimage.ReviewImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ReviewResponseDto {

    private Long reviewId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean isWriteMe;

    private String nickname;
    private int starScore;
    private String content;
    private boolean revisitYn;
    private int likeCnt;
    private List<String> images;
    private List<Integer> recommends;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일")
    private LocalDateTime createDate;

    @Builder
    public ReviewResponseDto(Long reviewId, boolean isWriteMe, String nickname, int starScore, String content, boolean revisitYn, int likeCnt, List<String> images, List<Integer> recommends, LocalDateTime createDate) {
        this.reviewId = reviewId;
        this.isWriteMe = isWriteMe;
        this.nickname = nickname;
        this.starScore = starScore;
        this.content = content;
        this.revisitYn = revisitYn;
        this.likeCnt = likeCnt;
        this.images = images;
        this.recommends = recommends;
        this.createDate = createDate;
    }

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getId();
        this.nickname = review.getUser().getNickname();
        this.starScore = review.getStarScore();
        this.content = review.getContent();
        this.likeCnt = review.getLikeCnt();
        this.revisitYn = review.isRevisitYn();
        this.images = review.getReviewImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList());
        this.recommends = review.getReviewRecommends().stream().map(v -> Integer.parseInt(v.getRecommendType().getCode())).collect(Collectors.toList());
        this.createDate = review.getCreateDatetime();
    }

    public boolean getIsWriteMe() {
        return this.isWriteMe;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setRecommends(List<Integer> recommends) {
        this.recommends = recommends;
    }
}
