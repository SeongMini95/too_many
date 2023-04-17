package com.ojeomme.domain.store.repository;

import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto.StoreResponseDto;

import java.util.Optional;

public interface StoreCustomRepository {

    Optional<StoreResponseDto> getStore(Long storeId);
}
