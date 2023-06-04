package com.ojeomme.domain.eattogetherpost.repository;

import com.ojeomme.domain.regioncode.QRegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.response.eattogether.EatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostResponseDto;
import com.ojeomme.dto.response.eattogether.RecentEatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.RecentEatTogetherPostListResponseDto.PostResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.ojeomme.domain.eattogetherpost.QEatTogetherPost.eatTogetherPost;
import static com.ojeomme.domain.eattogetherreply.QEatTogetherReply.eatTogetherReply;
import static com.ojeomme.domain.regioncode.QRegionCode.regionCode;
import static com.ojeomme.domain.user.QUser.user;

@RequiredArgsConstructor
public class EatTogetherPostCustomRepositoryImpl implements EatTogetherPostCustomRepository {

    private final JPAQueryFactory factory;
    private final RegionCodeRepository regionCodeRepository;

    private static final int POST_LIST_PAGE_SIZE = 30;

    @Override
    public Optional<EatTogetherPostResponseDto> getEatTogetherPost(Long userId, Long postId) {
        return Optional.ofNullable(factory
                .select(Projections.fields(
                        EatTogetherPostResponseDto.class,
                        eatTogetherPost.id.as("postId"),
                        new CaseBuilder()
                                .when(eatTogetherPost.user.id.eq(userId))
                                .then(true)
                                .otherwise(false).as("isWrite"),
                        eatTogetherPost.user.nickname,
                        eatTogetherPost.user.profile,
                        eatTogetherPost.regionCode.code.as("regionCode"),
                        eatTogetherPost.regionCode.regionName,
                        eatTogetherPost.subject,
                        eatTogetherPost.content,
                        eatTogetherPost.createDatetime
                ))
                .from(eatTogetherPost)
                .innerJoin(eatTogetherPost.user, user)
                .innerJoin(eatTogetherPost.regionCode, regionCode)
                .leftJoin(eatTogetherReply).on(eatTogetherPost.id.eq(eatTogetherReply.eatTogetherPost.id))
                .where(eatTogetherPost.id.eq(postId))
                .groupBy(eatTogetherPost.id)
                .fetchOne());
    }

    @Override
    public EatTogetherPostListResponseDto getEatTogetherPostList(String code, Long moreId) {
        // 하위 지역 코드 가져오기
        Set<String> regionCodes = regionCodeRepository.getDownCode(code);

        BooleanBuilder ltPostId = new BooleanBuilder();
        if (moreId != null) {
            ltPostId.and(eatTogetherPost.id.lt(moreId));
        }

        return new EatTogetherPostListResponseDto(factory
                .select(Projections.fields(
                        EatTogetherPostListResponseDto.PostResponseDto.class,
                        eatTogetherPost.id.as("postId"),
                        eatTogetherPost.user.nickname,
                        eatTogetherPost.regionCode.regionName,
                        eatTogetherPost.subject,
                        eatTogetherReply.count().as("replyCnt"),
                        eatTogetherPost.createDatetime.as("oriCreateDatetime")
                ))
                .from(eatTogetherPost)
                .innerJoin(eatTogetherPost.regionCode, regionCode)
                .innerJoin(eatTogetherPost.user, user)
                .leftJoin(eatTogetherReply).on(
                        eatTogetherPost.id.eq(eatTogetherReply.eatTogetherPost.id),
                        eatTogetherReply.deleteYn.eq(false)
                )
                .where(
                        ltPostId,
                        eatTogetherPost.regionCode.code.in(regionCodes)
                )
                .groupBy(eatTogetherPost.id)
                .orderBy(eatTogetherPost.id.desc())
                .limit(POST_LIST_PAGE_SIZE)
                .fetch());
    }

    @Override
    public RecentEatTogetherPostListResponseDto getRecentEatTogetherPostList(String regionCode) {
        // 하위 지역 코드 가져오기
        Set<String> regionCodes = regionCodeRepository.getDownCode(regionCode);

        List<PostResponseDto> posts = factory
                .select(Projections.fields(
                        PostResponseDto.class,
                        eatTogetherPost.id.as("postId"),
                        eatTogetherPost.regionCode.regionName,
                        eatTogetherPost.subject,
                        eatTogetherPost.createDatetime.as("ltCreateDatetime")
                ))
                .from(eatTogetherPost)
                .innerJoin(eatTogetherPost.regionCode, QRegionCode.regionCode)
                .where(QRegionCode.regionCode.code.in(regionCodes))
                .orderBy(eatTogetherPost.id.desc())
                .limit(10)
                .fetch();
        posts.forEach(PostResponseDto::convertDatetime);

        return new RecentEatTogetherPostListResponseDto(posts);
    }
}
