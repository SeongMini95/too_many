package com.ojeomme.domain.storereviewstatistics;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.store.Store;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "store_review_statistics")
public class StoreReviewStatistics extends BaseTimeEntity {

    @EmbeddedId
    private StoreReviewStatisticsId id;

    @MapsId("storeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "avg_score", nullable = false)
    private float avgScore;

    @Column(name = "review_cnt", nullable = false)
    private int reviewCnt;

    @Builder
    public StoreReviewStatistics(StoreReviewStatisticsId id, Store store, float avgScore, int reviewCnt) {
        this.id = id;
        this.store = store;
        this.avgScore = avgScore;
        this.reviewCnt = reviewCnt;
    }
}