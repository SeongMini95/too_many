package com.toomany.domain.reviewrecommend;

import com.toomany.domain.BaseTimeEntity;
import com.toomany.domain.review.Review;
import com.toomany.domain.reviewrecommend.enums.RecommendType;
import com.toomany.domain.reviewrecommend.enums.converter.RecommendTypeConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "review_recommend")
public class ReviewRecommend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Convert(converter = RecommendTypeConverter.class)
    @Column(name = "recommend_type", nullable = false)
    private RecommendType recommendType;

    @Builder
    public ReviewRecommend(Review review, RecommendType recommendType) {
        this.review = review;
        this.recommendType = recommendType;
    }
}