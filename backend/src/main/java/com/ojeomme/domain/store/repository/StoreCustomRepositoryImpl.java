package com.ojeomme.domain.store.repository;

import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.response.store.RealTimeStoreRankingResponseDto;
import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto.StoreResponseDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ojeomme.domain.regioncode.QRegionCode.regionCode;
import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;
import static com.ojeomme.domain.store.QStore.store;

@RequiredArgsConstructor
public class StoreCustomRepositoryImpl implements StoreCustomRepository {

    private final JPAQueryFactory factory;
    private final RegionCodeRepository regionCodeRepository;

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

    @Override
    public RealTimeStoreRankingResponseDto getRealTimeStoreRanking(String code) {
        Set<String> regionCodes = regionCodeRepository.getDownCode(code);

        List<RealTimeStoreRankingResponseDto.StoreResponseDto> stores = factory
                .select(Projections.fields(
                        RealTimeStoreRankingResponseDto.StoreResponseDto.class,
                        store.id.as("storeId"),
                        store.storeName,
                        regionCode.regionName
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
                .fetch();

        // 좋아요 많이 받은 리뷰 가져오기
        List<Tuple> maxLikeReviews = factory
                .select(
                        review.likeCnt.max(),
                        review.store.id
                )
                .from(review)
                .innerJoin(review.reviewImages, reviewImage)
                .where(review.store.id.in(stores.stream().map(RealTimeStoreRankingResponseDto.StoreResponseDto::getStoreId).collect(Collectors.toList())))
                .groupBy(review.store.id)
                .fetch();

        // 이미지 설정
        stores.forEach(v -> {
            Tuple tuple = maxLikeReviews.stream()
                    .filter(v2 -> Objects.equals(v2.get(1, Long.class), v.getStoreId()))
                    .findFirst()
                    .orElse(null);

            if (tuple != null) {
                String image = factory
                        .select(reviewImage.imageUrl)
                        .from(review)
                        .innerJoin(review.reviewImages, reviewImage)
                        .where(
                                review.store.id.eq(tuple.get(1, Long.class)),
                                review.likeCnt.eq(tuple.get(0, Integer.class))
                        )
                        .orderBy(reviewImage.id.desc())
                        .fetchFirst();

                v.setImage(image);
            }
        });

        return new RealTimeStoreRankingResponseDto(stores);
    }
}
