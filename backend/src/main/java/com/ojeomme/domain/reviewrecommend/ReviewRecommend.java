package com.ojeomme.domain.reviewrecommend;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.domain.reviewrecommend.enums.converter.RecommendTypeConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReviewRecommend that = (ReviewRecommend) o;
        return getId() != null && (Objects.equals(getId(), that.getId()) ||
                (Objects.equals(getReview().getId(), that.getReview().getId()) &&
                        Objects.equals(getRecommendType(), that.getRecommendType())));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}