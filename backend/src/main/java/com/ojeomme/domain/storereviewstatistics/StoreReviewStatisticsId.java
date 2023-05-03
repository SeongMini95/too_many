package com.ojeomme.domain.storereviewstatistics;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class StoreReviewStatisticsId implements Serializable {

    @Column(name = "statistics_date", nullable = false)
    private LocalDate statisticsDate;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Builder
    public StoreReviewStatisticsId(LocalDate statisticsDate, Long storeId) {
        this.statisticsDate = statisticsDate;
        this.storeId = storeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StoreReviewStatisticsId entity = (StoreReviewStatisticsId) o;
        return Objects.equals(this.statisticsDate, entity.statisticsDate) &&
                Objects.equals(this.storeId, entity.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statisticsDate, storeId);
    }
}