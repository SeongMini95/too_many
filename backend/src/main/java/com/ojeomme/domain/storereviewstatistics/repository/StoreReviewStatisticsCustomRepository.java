package com.ojeomme.domain.storereviewstatistics.repository;

import com.ojeomme.dto.response.storereviewstatistics.TodayStoreRankingResponseDto;

public interface StoreReviewStatisticsCustomRepository {

    TodayStoreRankingResponseDto getTodayStoreReviewRanking(String regionCode);
}
