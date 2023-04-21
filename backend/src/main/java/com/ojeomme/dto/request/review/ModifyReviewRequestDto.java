package com.ojeomme.dto.request.review;

import com.ojeomme.common.enums.EnumCodeConverterUtils;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ModifyReviewRequestDto {

    @NotNull(message = "리뷰를 작성하세요.")
    @NotBlank(message = "리뷰를 작성하세요.")
    @Size(min = 10, max = 2000, message = "리뷰는 10자 ~ 2000자 이내로 작성하세요.")
    private String content;

    @NotNull(message = "별점을 입력하세요.")
    @Min(value = 1, message = "별점은 최소 1점 입니다.")
    @Max(value = 5, message = "별점은 최대 5점 입니다.")
    private Integer starScore;

    private boolean revisitYn;

    private List<@URL(message = "이미지 URL 형식이 올바르지 않습니다.") String> images = new ArrayList<>();

    private List<String> recommends = new ArrayList<>();

    @Builder
    public ModifyReviewRequestDto(String content, Integer starScore, boolean revisitYn, List<String> images, List<String> recommends) {
        this.content = content;
        this.starScore = starScore;
        this.revisitYn = revisitYn;
        this.images = images;
        this.recommends = recommends;
    }

    public Review toReview(Review review, List<String> images) {
        this.images = images;
        Review modifyReview = Review.builder()
                .starScore(starScore)
                .content(content)
                .revisitYn(revisitYn)
                .build();

        modifyReview.addImages(toReviewImages(review));
        modifyReview.addRecommends(toReviewRecommends(review));

        return modifyReview;
    }

    private Set<ReviewImage> toReviewImages(Review review) {
        return images.stream()
                .filter(StringUtils::isNotBlank)
                .map(v -> ReviewImage.builder()
                        .review(review)
                        .imageUrl(v)
                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<ReviewRecommend> toReviewRecommends(Review review) {
        return recommends.stream()
                .filter(StringUtils::isNotBlank)
                .map(v -> ReviewRecommend.builder()
                        .review(review)
                        .recommendType(EnumCodeConverterUtils.ofCode(v, RecommendType.class))
                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
