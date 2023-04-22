package com.ojeomme.domain.reviewlikelog;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "review_like_log")
public class ReviewLikeLog extends BaseTimeEntity implements Persistable<ReviewLikeLogId> {

    @EmbeddedId
    private ReviewLikeLogId id;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public ReviewLikeLog(Review review, User user) {
        this.review = review;
        this.user = user;
        this.id = ReviewLikeLogId.builder()
                .reviewId(review.getId())
                .userId(user.getId())
                .build();
    }

    @Override
    public boolean isNew() {
        return getCreateDatetime() == null;
    }
}
