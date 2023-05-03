package com.ojeomme.domain.storereviewstatistics.repository;

import com.ojeomme.domain.regioncode.QRegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.response.storereviewstatistics.TodayStoreRankingResponseDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;
import static com.ojeomme.domain.store.QStore.store;
import static com.ojeomme.domain.storereviewstatistics.QStoreReviewStatistics.storeReviewStatistics;

@RequiredArgsConstructor
public class StoreReviewStatisticsCustomRepositoryImpl implements StoreReviewStatisticsCustomRepository {

    private final JPAQueryFactory factory;
    private final RegionCodeRepository regionCodeRepository;

    @Override
    public TodayStoreRankingResponseDto getTodayStoreReviewRanking(String regionCode) {
        Set<String> regionCodes = regionCodeRepository.getDownCode(regionCode);

        List<TodayStoreRankingResponseDto.StoreResponseDto> stores = factory
                .select(Projections.fields(
                        TodayStoreRankingResponseDto.StoreResponseDto.class,
                        storeReviewStatistics.store.id.as("storeId"),
                        store.storeName,
                        QRegionCode.regionCode.regionName
                ))
                .from(storeReviewStatistics)
                .innerJoin(store).on(storeReviewStatistics.store.id.eq(store.id))
                .innerJoin(QRegionCode.regionCode).on(store.regionCode.code.eq(QRegionCode.regionCode.code))
                .where(
                        storeReviewStatistics.id.statisticsDate.eq(LocalDate.now()),
                        QRegionCode.regionCode.code.in(regionCodes)
                )
                .orderBy(
                        storeReviewStatistics.avgScore.desc(),
                        storeReviewStatistics.reviewCnt.desc()
                )
                .limit(10)
                .fetch();
        stores.forEach(v -> {
            Tuple image = factory
                    .select(
                            review.likeCnt.max(),
                            reviewImage.imageUrl.as("image")
                    )
                    .from(review)
                    .leftJoin(reviewImage).on(review.id.eq(reviewImage.review.id))
                    .where(review.store.id.eq(v.getStoreId()))
                    .groupBy(review.store.id)
                    .fetchOne();

            if (image != null) {
                v.setImage(image.get(reviewImage.imageUrl.as("image")));
            }
        });

        return new TodayStoreRankingResponseDto(stores);
    }
}
