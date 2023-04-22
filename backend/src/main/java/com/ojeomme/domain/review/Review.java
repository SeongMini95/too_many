package com.ojeomme.domain.review;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "reviews")
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "star_score", nullable = false)
    private int starScore;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "revisit_yn", nullable = false)
    private boolean revisitYn;

    @Column(name = "like_cnt", nullable = false)
    private int likeCnt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewImage> reviewImages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewRecommend> reviewRecommends = new LinkedHashSet<>();

    @Builder
    public Review(Long id, User user, Store store, int starScore, String content, boolean revisitYn, int likeCnt) {
        this.id = id;
        this.user = user;
        this.store = store;
        this.starScore = starScore;
        this.content = content;
        this.revisitYn = revisitYn;
        this.likeCnt = likeCnt;
    }

    public void addImages(Set<ReviewImage> reviewImages) {
        this.reviewImages.addAll(reviewImages);
    }

    public void addRecommends(Set<ReviewRecommend> reviewRecommends) {
        this.reviewRecommends.addAll(reviewRecommends);
    }

    public void modifyReview(Review review) {
        this.starScore = review.getStarScore();
        this.content = review.getContent();
        this.revisitYn = review.isRevisitYn();

        Collection<ReviewImage> minusImages = CollectionUtils.subtract(this.reviewImages, review.getReviewImages());
        this.reviewImages.addAll(review.getReviewImages());
        this.reviewImages.removeAll(minusImages);

        Collection<ReviewRecommend> minusRecommends = CollectionUtils.subtract(this.reviewRecommends, review.getReviewRecommends());
        this.reviewRecommends.addAll(review.getReviewRecommends());
        this.reviewRecommends.removeAll(minusRecommends);
    }

    public void like() {
        this.likeCnt += 1;
    }

    public void cancelLike() {
        this.likeCnt -= 1;
    }
}