package com.ojeomme.domain.review.repository;

import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;
import static com.ojeomme.domain.reviewrecommend.QReviewRecommend.reviewRecommend;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public ReviewListResponseDto getReviewList(Long storeId, Long reviewId) {
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

        // 페이징
        BooleanBuilder ltReviewId = new BooleanBuilder();
        if (reviewId != null) {
            ltReviewId.and(review.id.lt(reviewId));
        }

        List<ReviewListResponseDto.ReviewResponseDto> getReviewList = factory
                .from(review)
                .innerJoin(review.user)
                .leftJoin(review.reviewImages, reviewImage)
                .leftJoin(review.reviewRecommends, reviewRecommend)
                .where(
                        review.store.id.eq(storeId),
                        ltReviewId
                )
                .orderBy(review.id.desc(), reviewImage.id.asc())
                .limit(5 * imageCnt * recommendCnt) // 5개씩 가져오기
                .transform(
                        groupBy(review.id)
                                .list(Projections.fields(
                                        ReviewListResponseDto.ReviewResponseDto.class,
                                        review.id.as("reviewId"),
                                        review.user.id.as("userId"),
                                        review.user.nickname,
                                        review.starScore,
                                        review.content,
                                        review.revisitYn,
                                        set(reviewImage.imageUrl).as("images"),
                                        set(reviewRecommend.recommendType.stringValue()).as("recommends"),
                                        review.createDatetime.as("createDate")
                                ))
                );

        return new ReviewListResponseDto(getReviewList);
    }
}
