package com.ojeomme.domain.eattogetherreply.repository;

import com.ojeomme.domain.eattogetherreply.QEatTogetherReply;
import com.ojeomme.domain.user.QUser;
import com.ojeomme.dto.response.eattogether.EatTogetherReplyListResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.ojeomme.domain.eattogetherreplyimage.QEatTogetherReplyImage.eatTogetherReplyImage;

@RequiredArgsConstructor
public class EatTogetherReplyCustomRepositoryImpl implements EatTogetherReplyCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public EatTogetherReplyListResponseDto getReplyList(Long postId) {
        QEatTogetherReply reply1 = new QEatTogetherReply("ori_reply");
        QEatTogetherReply reply2 = new QEatTogetherReply("ref_reply");
        QUser user1 = new QUser("ori_user");
        QUser user2 = new QUser("ref_user");

        return new EatTogetherReplyListResponseDto(factory
                .select(Projections.fields(
                        EatTogetherReplyListResponseDto.ReplyResponseDto.class,
                        reply1.id.as("replyId"),
                        user1.id.as("userId"),
                        user1.nickname,
                        new CaseBuilder()
                                .when(reply1.id.eq(reply1.upId)).then(Expressions.nullExpression(String.class))
                                .otherwise(user2.nickname).as("upNickname"),
                        reply1.content,
                        eatTogetherReplyImage.imageUrl.as("image"),
                        reply1.createDatetime
                ))
                .from(reply1)
                .innerJoin(user1).on(reply1.user.id.eq(user1.id))
                .innerJoin(reply2).on(reply1.upId.eq(reply2.id))
                .innerJoin(user2).on(reply2.user.id.eq(user2.id))
                .leftJoin(eatTogetherReplyImage).on(reply1.id.eq(eatTogetherReplyImage.eatTogetherReply.id))
                .where(reply1.eatTogetherPost.id.eq(postId))
                .orderBy(
                        reply1.upId.asc(),
                        reply1.id.asc()
                )
                .fetch());
    }
}
