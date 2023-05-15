package com.ojeomme.domain.userowncount;

import com.ojeomme.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user_own_count")
public class UserOwnCount {

    @Id
    private Long userId;

    @Column(name = "review_cnt", nullable = false)
    private int reviewCnt;

    @Column(name = "like_cnt", nullable = false)
    private int likeCnt;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public UserOwnCount(int reviewCnt, int likeCnt, User user) {
        this.userId = user.getId();
        this.reviewCnt = reviewCnt;
        this.likeCnt = likeCnt;
        this.user = user;
    }

    public void increaseReview() {
        this.reviewCnt += 1;
    }

    public void decreaseReview() {
        this.reviewCnt -= 1;
    }

    public void increaseLike() {
        this.likeCnt += 1;
    }

    public void decreaseLike(int cnt) {
        this.likeCnt -= cnt;
    }
}