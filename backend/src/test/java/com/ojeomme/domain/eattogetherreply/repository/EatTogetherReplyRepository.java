package com.ojeomme.domain.eattogetherreply.repository;

import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Profile("test")
public interface EatTogetherReplyRepository extends JpaRepository<EatTogetherReply, Long> {

    @Query(value = "VALUES NEXT VALUE FOR eat_together_reply_seq", nativeQuery = true)
    Long nextval();
}