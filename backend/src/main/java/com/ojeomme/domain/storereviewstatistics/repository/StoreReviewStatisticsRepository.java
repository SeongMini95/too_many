package com.ojeomme.domain.storereviewstatistics.repository;

import com.ojeomme.domain.storereviewstatistics.StoreReviewStatistics;
import com.ojeomme.domain.storereviewstatistics.StoreReviewStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreReviewStatisticsRepository extends JpaRepository<StoreReviewStatistics, StoreReviewStatisticsId>, StoreReviewStatisticsCustomRepository {
}