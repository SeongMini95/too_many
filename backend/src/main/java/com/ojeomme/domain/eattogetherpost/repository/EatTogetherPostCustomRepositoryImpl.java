package com.ojeomme.domain.eattogetherpost.repository;

import com.ojeomme.domain.regioncode.QRegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.response.eattogether.EatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

import static com.ojeomme.domain.eattogetherpost.QEatTogetherPost.eatTogetherPost;
import static com.ojeomme.domain.user.QUser.user;

@RequiredArgsConstructor
public class EatTogetherPostCustomRepositoryImpl implements EatTogetherPostCustomRepository {

    private final JPAQueryFactory factory;
    private final RegionCodeRepository regionCodeRepository;

    @Override
    public Optional<EatTogetherPostResponseDto> getEatTogetherPost(Long postId) {
        return Optional.ofNullable(factory
                .select(Projections.fields(
                        EatTogetherPostResponseDto.class,
                        eatTogetherPost.id,
                        eatTogetherPost.user.id.as("userId"),
                        eatTogetherPost.user.nickname,
                        eatTogetherPost.regionCode.code.as("regionCode"),
                        eatTogetherPost.regionCode.regionName,
                        eatTogetherPost.subject,
                        eatTogetherPost.content,
                        eatTogetherPost.createDatetime
                ))
                .from(eatTogetherPost)
                .where(eatTogetherPost.id.eq(postId))
                .fetchOne());
    }

    @Override
    public EatTogetherPostListResponseDto getEatTogetherPostList(String regionCode, Long moreId) {
        // 하위 지역 코드 가져오기
        Set<String> regionCodes = regionCodeRepository.getDownCode(regionCode);

        BooleanBuilder ltPostId = new BooleanBuilder();
        if (moreId != null) {
            ltPostId.and(eatTogetherPost.id.lt(moreId));
        }

        return new EatTogetherPostListResponseDto(factory
                .select(Projections.fields(
                        EatTogetherPostListResponseDto.PostResponseDto.class,
                        eatTogetherPost.id,
                        eatTogetherPost.user.nickname,
                        eatTogetherPost.regionCode.regionName,
                        eatTogetherPost.subject,
                        eatTogetherPost.createDatetime.as("oriCreateDatetime")
                ))
                .from(eatTogetherPost)
                .innerJoin(eatTogetherPost.regionCode, QRegionCode.regionCode)
                .innerJoin(eatTogetherPost.user, user)
                .where(
                        ltPostId,
                        eatTogetherPost.regionCode.code.in(regionCodes)
                )
                .orderBy(eatTogetherPost.id.desc())
                .limit(30)
                .fetch());
    }
}
