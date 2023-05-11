package com.ojeomme.domain.review.repository;

import com.ojeomme.domain.review.Review;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.dto.response.review.ReviewResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ojeomme.domain.review.QReview.review;
import static com.ojeomme.domain.reviewimage.QReviewImage.reviewImage;
import static com.ojeomme.domain.reviewrecommend.QReviewRecommend.reviewRecommend;
import static com.ojeomme.domain.user.QUser.user;

@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public ReviewListResponseDto getReviewList(Long userId, Long storeId, Long moreId) {
        // noOffset 페이징
        BooleanBuilder ltReviewId = new BooleanBuilder();
        if (moreId != null) {
            ltReviewId.and(review.id.lt(moreId));
        }

        List<ReviewResponseDto> reviews = factory
                .select(Projections.fields(
                        ReviewResponseDto.class,
                        review.id.as("reviewId"),
                        Expressions.booleanPath(String.valueOf(review.user.id.equals(userId))).as("isWriteMe"),
                        review.user.nickname,
                        review.starScore,
                        review.content,
                        review.revisitYn,
                        review.likeCnt,
                        review.createDatetime.as("createDate")
                ))
                .from(review)
                .innerJoin(review.user, user)
                .where(
                        review.store.id.eq(storeId),
                        ltReviewId
                )
                .orderBy(review.id.desc())
                .limit(5)
                .fetch();
        List<Long> reviewIds = reviews.stream()
                .map(ReviewResponseDto::getReviewId)
                .collect(Collectors.toList());

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

        // 이미지, 추천 포인트 설정
        reviews.forEach(v -> {
            List<String> images = reviewImages.stream()
                    .filter(v2 -> Objects.equals(v2.get(reviewImage.review.id), v.getReviewId()))
                    .map(v2 -> v2.get(reviewImage.imageUrl))
                    .collect(Collectors.toList());
            List<Integer> recommends = reviewRecommends.stream()
                    .filter(v2 -> Objects.equals(v2.get(reviewRecommend.review.id), v.getReviewId()))
                    .map(v2 -> Integer.parseInt(v2.get(reviewRecommend.recommendType).getCode()))
                    .collect(Collectors.toList());

            v.setImages(images);
            v.setRecommends(recommends);
        });

        return new ReviewListResponseDto(reviews);
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
}
