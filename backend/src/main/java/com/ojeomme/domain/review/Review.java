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

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "star_score")
    private int starScore;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "revisit_yn", nullable = false)
    private boolean revisitYn;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewRecommend> reviewRecommends = new ArrayList<>();

    @Builder
    public Review(User user, Store store, int starScore, String content, boolean revisitYn) {
        this.user = user;
        this.store = store;
        this.starScore = starScore;
        this.content = content;
        this.revisitYn = revisitYn;
    }

    public void addImages(List<ReviewImage> reviewImages) {
        this.reviewImages.clear();
        this.reviewImages.addAll(reviewImages);
    }

    public void addRecommends(List<ReviewRecommend> reviewRecommends) {
        this.reviewRecommends.clear();
        this.reviewRecommends.addAll(reviewRecommends);
    }
}