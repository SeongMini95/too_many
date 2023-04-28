package com.ojeomme.domain.eattogetherreply.repository;

import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EatTogetherReplyRepository extends JpaRepository<EatTogetherReply, Long>, EatTogetherReplyCustomRepository {

    @Query(value = "select next value for eat_together_reply_seq", nativeQuery = true)
    Long nextval();

    Optional<EatTogetherReply> findByIdAndUserIdAndEatTogetherPostId(Long replyId, Long userId, Long postId);
}