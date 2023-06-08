package com.ojeomme.domain.eattogetherpost.repository;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EatTogetherPostRepository extends JpaRepository<EatTogetherPost, Long>, EatTogetherPostCustomRepository {

    Optional<EatTogetherPost> findByIdAndUserId(Long postId, Long userId);

    List<EatTogetherPost> findTop10ByRegionCodeCodeInOrderByIdDesc(Set<String> regionCodes);
}