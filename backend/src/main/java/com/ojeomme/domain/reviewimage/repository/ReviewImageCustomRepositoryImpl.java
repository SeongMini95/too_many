package com.ojeomme.domain.reviewimage.repository;

import com.ojeomme.dto.response.reviewimage.PreviewImageListResponseDto;
import com.ojeomme.dto.response.reviewimage.ReviewImageListResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;

@RequiredArgsConstructor
public class ReviewImageCustomRepositoryImpl implements ReviewImageCustomRepository {

    private final JPAQueryFactory factory;

    private static final int IMAGE_LIST_PAGE_SIZE = 20;

    @Override
    public PreviewImageListResponseDto getPreviewImageList(Long storeId) {
        long imageCnt = factory
                .select(reviewImage.count())
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .where(reviewImage.review.store.id.eq(storeId))
                .fetchFirst();

        List<String> images = factory
                .select(reviewImage.imageUrl)
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .where(reviewImage.review.store.id.eq(storeId))
                .orderBy(
                        reviewImage.review.id.desc(),
                        reviewImage.id.asc()
                )
                .limit(5)
                .fetch();

        return new PreviewImageListResponseDto(imageCnt, images);
    }

    @Override
    public ReviewImageListResponseDto getReviewImageList(Long storeId, Long moreId) {
        // 페이징
        BooleanBuilder ltReviewImageId = new BooleanBuilder();
        if (moreId != null) {
            ltReviewImageId.and(reviewImage.id.lt(moreId));
        }

        long imageCnt = factory
                .select(reviewImage.count())
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .where(
                        reviewImage.review.store.id.eq(storeId),
                        ltReviewImageId
                )
                .fetchFirst();

        List<Tuple> reviewImages = factory
                .select(
                        reviewImage.id,
                        reviewImage.imageUrl
                )
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .where(
                        reviewImage.review.store.id.eq(storeId),
                        ltReviewImageId
                )
                .orderBy(
                        reviewImage.review.id.desc(),
                        reviewImage.id.desc()
                )
                .limit(IMAGE_LIST_PAGE_SIZE)
                .fetch();

        List<String> images = reviewImages.stream()
                .map(v -> v.get(reviewImage.imageUrl))
                .collect(Collectors.toList());

        if (imageCnt <= IMAGE_LIST_PAGE_SIZE) {
            return new ReviewImageListResponseDto(true, null, images);
        }

        return new ReviewImageListResponseDto(false, reviewImages.get(reviewImages.size() - 1).get(reviewImage.id), images);
    }
}
