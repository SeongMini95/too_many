package com.ojeomme.domain.eattogetherreply.repository;

import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EatTogetherReplyRepository extends JpaRepository<EatTogetherReply, Long> {

    @Query(value = "select nextval(eat_together_reply_seq)", nativeQuery = true)
    Long nextval();

    @Modifying
    @Query(value = "insert into eat_together_reply values (:id, :userId, :postId, :upId, :content, now(), now())", nativeQuery = true)
    void insert(Long id, Long userId, Long postId, Long upId, String content);
}