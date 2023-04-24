package com.ojeomme.domain.reviewimage.repository;

import com.ojeomme.dto.response.store.ReviewImageListResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;

@RequiredArgsConstructor
public class ReviewImageCustomRepositoryImpl implements ReviewImageCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public ReviewImageListResponseDto getReviewImageList(Long storeId, Long reviewImageId) {
        // 페이징
        BooleanBuilder ltReviewImageId = new BooleanBuilder();
        if (reviewImageId != null) {
            ltReviewImageId.and(reviewImage.id.lt(reviewImageId));
        }

        List<Tuple> reviewImages = factory
                .select(
                        reviewImage.id,
                        reviewImage.imageUrl
                )
                .from(reviewImage)
                .innerJoin(reviewImage.review)
                .where(
                        reviewImage.review.store.id.eq(storeId),
                        ltReviewImageId
                )
                .orderBy(
                        reviewImage.review.id.desc(),
                        reviewImage.id.desc()
                )
                .limit(20)
                .fetch();

        Long moreId = reviewImages.isEmpty() ? 0L : reviewImages.get(reviewImages.size() - 1).get(reviewImage.id);

        return new ReviewImageListResponseDto(reviewImages.stream()
                .map(v -> v.get(reviewImage.imageUrl))
                .collect(Collectors.toList()), moreId);
    }
}
