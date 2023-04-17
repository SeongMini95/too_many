package com.ojeomme.domain.store.repository;

import com.ojeomme.dto.response.review.ReviewListResponseDto.ReviewResponseDto;
import com.ojeomme.dto.response.store.StoreReviewsResponseDto;
import com.ojeomme.dto.response.store.StoreReviewsResponseDto.StoreResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;
import static com.ojeomme.domain.reviewrecommend.QReviewRecommend.reviewRecommend;
import static com.ojeomme.domain.store.QStore.store;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;

@RequiredArgsConstructor
public class StoreCustomRepositoryImpl implements StoreCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Optional<StoreReviewsResponseDto> getStoreReview(Long storeId) {
        StoreResponseDto getStore = factory
                .select(Projections.fields(
                        StoreResponseDto.class,
                        store.id.as("storeId"),
                        store.kakaoPlaceId.as("placeId"),
                        store.storeName,
                        store.category.categoryName,
                        store.addressName,
                        store.roadAddressName,
                        store.likeCnt
                ))
                .from(store)
                .where(store.id.eq(storeId))
                .fetchOne();
        if (getStore == null) {
            return Optional.empty();
        }

        List<String> getPreviewImageList = factory
                .select(reviewImage.imageUrl)
                .from(review)
                .innerJoin(review.reviewImages, reviewImage)
                .where(review.store.id.eq(storeId))
                .orderBy(review.id.desc(), reviewImage.id.asc())
                .limit(5)
                .fetch();

        long imageCnt = factory
                .select(review.count())
                .from(review)
                .leftJoin(review.reviewImages)
                .where(review.store.id.eq(storeId))
                .fetchFirst();

        long recommendCnt = factory
                .select(review.count())
                .from(review)
                .leftJoin(review.reviewRecommends)
                .where(review.store.id.eq(storeId))
                .fetchFirst();

        List<ReviewResponseDto> getReviewList = factory
                .from(review)
                .innerJoin(review.user)
                .leftJoin(review.reviewImages, reviewImage)
                .leftJoin(review.reviewRecommends, reviewRecommend)
                .where(review.store.id.eq(storeId))
                .orderBy(review.id.desc())
                .limit(5 * imageCnt * recommendCnt)
                .transform(
                        groupBy(review.id)
                                .list(Projections.fields(
                                        ReviewResponseDto.class,
                                        review.id.as("reviewId"),
                                        review.user.nickname,
                                        review.starScore,
                                        review.content,
                                        review.revisitYn,
                                        set(reviewImage.imageUrl).as("images"),
                                        set(reviewRecommend.recommendType.stringValue()).as("recommends")
                                ))
                );

        return Optional.of(new StoreReviewsResponseDto(getStore, getPreviewImageList, getReviewList));
    }
}
