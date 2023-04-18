package com.ojeomme.domain.reviewimage;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.review.Review;
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
@Table(name = "review_image")
public class ReviewImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "image_url", nullable = false, length = 2083)
    private String imageUrl;

    @Builder
    public ReviewImage(Review review, String imageUrl) {
        this.review = review;
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReviewImage that = (ReviewImage) o;
        return getId() != null && (Objects.equals(getId(), that.getId()) ||
                (Objects.equals(getReview().getId(), that.getReview().getId()) &&
                        Objects.equals(getImageUrl(), that.getImageUrl())));
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}