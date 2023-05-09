package com.ojeomme.dto.request.review;

import com.ojeomme.common.enums.EnumCodeConverterUtils;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.user.User;
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
public class WriteReviewRequestDto {

    @NotNull(message = "리뷰를 작성하세요.")
    @NotBlank(message = "리뷰를 작성하세요.")
    @Size(min = 10, max = 2000, message = "리뷰는 10자 ~ 2000자 이내로 작성하세요.")
    private String content;

    @NotNull(message = "별점을 입력하세요.")
    @Min(value = 1, message = "별점은 최소 1점 입니다.")
    @Max(value = 5, message = "별점은 최대 5점 입니다.")
    private Integer starScore;

    private boolean revisitYn;

    private List<@URL(message = "이미지 URL 형식이 올바르지 않습니다.") String> images;

    private List<String> recommends;

    @NotNull(message = "지역을 선택하세요.")
    @NotBlank(message = "지역을 선택하세요.")
    private String x;

    @NotNull(message = "지역을 선택하세요.")
    @NotBlank(message = "지역을 선택하세요.")
    private String y;

    @Builder
    public WriteReviewRequestDto(String content, Integer starScore, boolean revisitYn, List<String> images, List<String> recommends, String x, String y) {
        this.content = content;
        this.starScore = starScore;
        this.revisitYn = revisitYn;
        this.images = images != null ? images : new ArrayList<>();
        this.recommends = recommends != null ? recommends : new ArrayList<>();
        this.x = x;
        this.y = y;
    }

    public Review toReview(User user, Store store, List<String> images) {
        this.images = images;
        Review review = Review.builder()
                .user(user)
                .store(store)
                .starScore(starScore)
                .content(content)
                .revisitYn(revisitYn)
                .likeCnt(0)
                .build();
        review.addImages(toReviewImages(review));
        review.addRecommends(toReviewRecommends(review));

        return review;
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
