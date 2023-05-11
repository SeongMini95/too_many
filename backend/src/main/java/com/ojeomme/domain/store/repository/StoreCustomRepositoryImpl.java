package com.ojeomme.domain.store.repository;

import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.response.store.RealTimeStoreRankingResponseDto;
import com.ojeomme.dto.response.store.StoreListResponseDto;
import com.ojeomme.dto.response.store.StoreResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

import static com.ojeomme.domain.category.QCategory.category;
import static com.ojeomme.domain.regioncode.QRegionCode.regionCode;
import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.store.QStore.store;

@RequiredArgsConstructor
public class StoreCustomRepositoryImpl implements StoreCustomRepository {

    private final JPAQueryFactory factory;
    private final RegionCodeRepository regionCodeRepository;
    private final CategoryRepository categoryRepository;

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
                                store.regionCode.regionName,
                                store.addressName,
                                store.roadAddressName,
                                store.x,
                                store.y,
                                store.likeCnt,
                                review.count().as("reviewCnt"),
                                MathExpressions.round(review.starScore.avg(), 2).as("avgStarScore")
                        ))
                        .from(store)
                        .innerJoin(store.regionCode, regionCode)
                        .innerJoin(store.category, category)
                        .innerJoin(store.reviews, review)
                        .where(store.id.eq(storeId))
                        .groupBy(store.id)
                        .fetchOne()
        );
    }

    @Override
    public RealTimeStoreRankingResponseDto getRealTimeStoreRanking(String code) {
        Set<String> regionCodes = regionCodeRepository.getDownCode(code);

        return new RealTimeStoreRankingResponseDto(factory
                .select(Projections.fields(
                        RealTimeStoreRankingResponseDto.StoreResponseDto.class,
                        store.id.as("storeId"),
                        store.storeName,
                        regionCode.regionName,
                        store.mainImageUrl.as("image")
                ))
                .from(store)
                .innerJoin(store.reviews, review)
                .innerJoin(store.regionCode, regionCode)
                .where(regionCode.code.in(regionCodes))
                .groupBy(store.id)
                .orderBy(
                        review.starScore.avg().desc(),
                        review.count().desc()
                )
                .limit(10)
                .fetch());
    }

    @Override
    public StoreListResponseDto getStoreList(String code, Long categoryId, Pageable pageable) {
        Set<String> regionCodes = regionCodeRepository.getDownCode(code);

        BooleanBuilder inCategories = new BooleanBuilder();
        if (categoryId != null) {
            Set<Long> categories = categoryRepository.getDownCategory(categoryId);
            inCategories.and(category.id.in(categories));
        }

        long totalCnt = factory
                .select(store.countDistinct())
                .from(store)
                .innerJoin(store.reviews, review)
                .innerJoin(store.regionCode, regionCode)
                .innerJoin(store.category, category)
                .where(
                        regionCode.code.in(regionCodes),
                        inCategories
                )
                .fetchFirst();

        boolean isEnd = totalCnt - ((long) pageable.getPageSize() * (pageable.getPageNumber() + 1)) <= 0;

        return new StoreListResponseDto(factory
                .select(Projections.fields(
                        StoreListResponseDto.StoreResponseDto.class,
                        store.id.as("storeId"),
                        store.storeName,
                        store.mainImageUrl.as("image"),
                        MathExpressions.round(review.starScore.avg(), 2).as("starScore"),
                        regionCode.regionName,
                        category.categoryName,
                        store.likeCnt,
                        review.id.count().as("reviewCnt")
                ))
                .from(store)
                .innerJoin(store.reviews, review)
                .innerJoin(store.regionCode, regionCode)
                .innerJoin(store.category, category)
                .where(
                        regionCode.code.in(regionCodes),
                        inCategories
                )
                .groupBy(store.id)
                .orderBy(review.starScore.avg().desc(), review.id.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(), pageable.getPageNumber() + 1, isEnd);
    }
}
