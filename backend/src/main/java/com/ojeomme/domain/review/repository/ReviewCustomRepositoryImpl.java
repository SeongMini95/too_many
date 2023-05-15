package com.ojeomme.domain.review.repository;

import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto.RecommendCount;
import com.ojeomme.dto.response.review.ReviewListResponseDto.ReviewResponseDto;
import com.ojeomme.dto.response.review.ReviewResponseDto.RecommendResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;
import static com.ojeomme.domain.reviewlikelog.QReviewLikeLog.reviewLikeLog;
import static com.ojeomme.domain.reviewrecommend.QReviewRecommend.reviewRecommend;
import static com.ojeomme.domain.user.QUser.user;
import static com.ojeomme.domain.userowncount.QUserOwnCount.userOwnCount;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory factory;

    private static final int REVIEW_LIST_PAGE_SIZE = 5;

    @Override
    public ReviewListResponseDto getReviewList(Long userId, Long storeId, Long moreId) {
        // no offset 페이징
        BooleanBuilder ltReviewId = new BooleanBuilder();
        if (moreId != null) {
            ltReviewId.and(review.id.lt(moreId));
        }

        List<ReviewResponseDto> reviews = getReviewResponseList(userId, storeId, ltReviewId);
        setReviewAddition(reviews);

        // 추천 포인트 갯수 건너뛰기
        if (moreId != null) {
            return new ReviewListResponseDto(reviews, null);
        }

        // 추천 포인트 갯수
        List<Tuple> totalRecommends = factory
                .select(
                        reviewRecommend.recommendType,
                        reviewRecommend.count()
                )
                .from(reviewRecommend)
                .innerJoin(reviewRecommend.review, review)
                .where(reviewRecommend.review.store.id.eq(storeId))
                .groupBy(reviewRecommend.recommendType)
                .orderBy(
                        reviewRecommend.count().desc(),
                        reviewRecommend.recommendType.asc()
                )
                .fetch();

        List<RecommendCount> recommendCounts = totalRecommends.stream()
                .map(v -> new RecommendCount(
                        Integer.parseInt(v.get(0, RecommendType.class).getCode()),
                        v.get(0, RecommendType.class).getDesc(),
                        v.get(1, Long.class)))
                .collect(Collectors.toList());

        return new ReviewListResponseDto(reviews, recommendCounts);
    }

    @Override
    public Optional<Review> getWithinAWeek(Long userId, Long placeId) {
        return Optional.ofNullable(factory
                .selectFrom(review)
                .where(
                        review.user.id.eq(userId),
                        review.store.kakaoPlaceId.eq(placeId)
                )
                .orderBy(review.id.desc())
                .limit(1)
                .fetchFirst());
    }

    @Override
    public ReviewListResponseDto getRefreshReviewList(Long userId, Long storeId, Long lastId) {
        BooleanBuilder gtReviewId = new BooleanBuilder();
        gtReviewId.and(review.id.goe(lastId));

        List<ReviewResponseDto> reviews = getReviewResponseList(userId, storeId, gtReviewId);
        setReviewAddition(reviews);

        return new ReviewListResponseDto(reviews, null);
    }

    private List<ReviewResponseDto> getReviewResponseList(Long userId, Long storeId, BooleanBuilder condition) {
        return factory
                .select(Projections.fields(
                        ReviewResponseDto.class,
                        review.id.as("reviewId"),
                        new CaseBuilder()
                                .when(user.id.eq(userId))
                                .then(true)
                                .otherwise(false).as("isWrite"),
                        user.nickname,
                        user.profile,
                        review.starScore,
                        review.content,
                        review.likeCnt,
                        review.createDatetime.as("createDate"),
                        userOwnCount.reviewCnt.as("userReviewCnt"),
                        userOwnCount.likeCnt.as("userLikeCnt"),
                        reviewLikeLog.user.id.isNotNull().as("isLike")
                ))
                .from(review)
                .innerJoin(review.user, user)
                .innerJoin(user.userOwnCount, userOwnCount)
                .leftJoin(reviewLikeLog).on(
                        reviewLikeLog.user.id.eq(userId),
                        reviewLikeLog.review.id.eq(review.id))
                .where(
                        review.store.id.eq(storeId),
                        condition
                )
                .orderBy(review.id.desc())
                .limit(REVIEW_LIST_PAGE_SIZE)
                .fetch();
    }

    private void setReviewAddition(List<ReviewResponseDto> reviews) {
        List<Long> reviewIds = reviews.stream()
                .map(ReviewResponseDto::getReviewId)
                .collect(Collectors.toList());

        // 이미지, 추천 포인트 설정
        List<Tuple> reviewImages = factory
                .select(
                        reviewImage.review.id,
                        reviewImage.imageUrl
                )
                .from(reviewImage)
                .where(reviewImage.review.id.in(reviewIds))
                .orderBy(reviewImage.review.id.desc(), reviewImage.id.asc())
                .fetch();
        List<Tuple> reviewRecommends = factory
                .select(
                        reviewRecommend.review.id,
                        reviewRecommend.recommendType
                )
                .from(reviewRecommend)
                .where(reviewRecommend.review.id.in(reviewIds))
                .orderBy(reviewRecommend.review.id.desc())
                .fetch();

        reviews.forEach(v -> {
            List<String> images = reviewImages.stream()
                    .filter(v2 -> Objects.equals(v2.get(reviewImage.review.id), v.getReviewId()))
                    .map(v2 -> v2.get(reviewImage.imageUrl))
                    .collect(Collectors.toList());
            List<RecommendResponseDto> recommends = reviewRecommends.stream()
                    .filter(v2 -> Objects.equals(v2.get(reviewRecommend.review.id), v.getReviewId()))
                    .map(v2 -> new RecommendResponseDto(
                            Integer.parseInt(v2.get(reviewRecommend.recommendType).getCode()),
                            v2.get(reviewRecommend.recommendType).getDesc()
                    ))
                    .collect(Collectors.toList());

            v.setImages(images);
            v.setRecommends(recommends);
        });
    }
}
