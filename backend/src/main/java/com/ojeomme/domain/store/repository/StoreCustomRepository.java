package com.ojeomme.domain.store.repository;

import com.ojeomme.dto.response.store.RealTimeStoreRankingResponseDto;
import com.ojeomme.dto.response.store.StoreListResponseDto;
import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto.StoreResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StoreCustomRepository {

    Optional<StoreResponseDto> getStore(Long storeId);

    RealTimeStoreRankingResponseDto getRealTimeStoreRanking(String code);

    StoreListResponseDto getStoreList(String code, Long categoryId, Pageable pageable);
}
