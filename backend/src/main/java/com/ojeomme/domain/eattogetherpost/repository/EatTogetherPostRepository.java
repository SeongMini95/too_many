package com.ojeomme.domain.eattogetherpost.repository;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EatTogetherPostRepository extends JpaRepository<EatTogetherPost, Long>, EatTogetherPostCustomRepository {

    Optional<EatTogetherPost> findByIdAndUserId(Long postId, Long userId);
}