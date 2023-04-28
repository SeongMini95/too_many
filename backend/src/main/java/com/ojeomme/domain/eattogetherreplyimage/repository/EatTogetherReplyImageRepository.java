package com.ojeomme.domain.eattogetherreplyimage.repository;

import com.ojeomme.domain.eattogetherreplyimage.EatTogetherReplyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EatTogetherReplyImageRepository extends JpaRepository<EatTogetherReplyImage, Long> {

    Optional<EatTogetherReplyImage> findByEatTogetherReplyId(Long replyId);
}