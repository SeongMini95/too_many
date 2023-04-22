package com.ojeomme.domain.reviewlikelog;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Embeddable
public class ReviewLikeLogId implements Serializable {

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public ReviewLikeLogId(Long reviewId, Long userId) {
        this.reviewId = reviewId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewLikeLogId that = (ReviewLikeLogId) o;
        return Objects.equals(reviewId, that.reviewId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, userId);
    }
}
