package com.toomany.domain.review;

import com.toomany.domain.BaseTimeEntity;
import com.toomany.domain.store.Store;
import com.toomany.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "revisit_yn", nullable = false)
    private boolean revisitYn;

    @Builder
    public Review(User user, Store store, String content, boolean revisitYn) {
        this.user = user;
        this.store = store;
        this.content = content;
        this.revisitYn = revisitYn;
    }
}