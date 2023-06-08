package com.ojeomme.domain.eattogetherreply.repository;

import com.ojeomme.domain.eattogetherreply.QEatTogetherReply;
import com.ojeomme.domain.user.QUser;
import com.ojeomme.dto.response.eattogether.EatTogetherReplyListResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ojeomme.domain.eattogetherreply.QEatTogetherReply.eatTogetherReply;

@RequiredArgsConstructor
public class EatTogetherReplyCustomRepositoryImpl implements EatTogetherReplyCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public EatTogetherReplyListResponseDto getReplyList(Long userId, Long postId) {
        QEatTogetherReply reply1 = new QEatTogetherReply("ori_reply");
        QEatTogetherReply reply2 = new QEatTogetherReply("ref_reply");
        QUser user1 = new QUser("ori_user");
        QUser user2 = new QUser("ref_user");

        long totalCnt = factory
                .select(reply1.count())
                .from(reply1)
                .where(
                        reply1.eatTogetherPost.id.eq(postId),
                        reply1.deleteYn.eq(false)
                )
                .fetchFirst();

        List<EatTogetherReplyListResponseDto.ReplyResponseDto> replies = factory
                .select(Projections.fields(
                        EatTogetherReplyListResponseDto.ReplyResponseDto.class,
                        reply1.id.as("replyId"),
                        new CaseBuilder()
                                .when(user1.id.eq(userId)).then(true)
                                .otherwise(false).as("isWrite"),
                        new CaseBuilder()
                                .when(reply1.eatTogetherPost.user.id.eq(reply1.user.id)).then(true)
                                .otherwise(false).as("isWriter"),
                        user1.nickname,
                        user1.profile,
                        reply2.id.as("upReplyId"),
                        new CaseBuilder()
                                .when(reply1.id.eq(reply1.upId)).then(Expressions.nullExpression(String.class))
                                .otherwise(user2.nickname).as("upNickname"),
                        reply1.content,
                        reply1.imageUrl.as("image"),
                        reply1.createDatetime
                ))
                .from(reply1)
                .innerJoin(reply1.eatTogetherPost)
                .innerJoin(user1).on(reply1.user.id.eq(user1.id))
                .innerJoin(reply2).on(reply1.upId.eq(reply2.id))
                .innerJoin(user2).on(reply2.user.id.eq(user2.id))
                .where(
                        reply1.eatTogetherPost.id.eq(postId),
                        reply1.deleteYn.eq(false)
                )
                .orderBy(
                        reply2.upId.asc(),
                        reply1.id.asc()
                )
                .fetch();

        return new EatTogetherReplyListResponseDto(totalCnt, replies);
    }

    @Override
    public boolean exists(Long replyId) {
        return factory
                .selectOne()
                .from(eatTogetherReply)
                .where(eatTogetherReply.id.eq(replyId))
                .fetchFirst() != null;
    }
}
