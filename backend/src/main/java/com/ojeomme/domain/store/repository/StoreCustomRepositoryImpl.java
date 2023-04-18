package com.ojeomme.domain.store.repository;

import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto.StoreResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.ojeomme.domain.store.QStore.store;

@RequiredArgsConstructor
public class StoreCustomRepositoryImpl implements StoreCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Optional<StoreResponseDto> getStore(Long storeId) {
        return Optional.ofNullable(
                factory
                        .select(Projections.fields(
                                StoreResponseDto.class,
                                store.id.as("storeId"),
                                store.kakaoPlaceId.as("placeId"),
                                store.storeName,
                                store.category.categoryName,
                                store.addressName,
                                store.roadAddressName,
                                store.x,
                                store.y,
                                store.likeCnt
                        ))
                        .from(store)
                        .where(store.id.eq(storeId))
                        .fetchOne()
        );
    }
}
