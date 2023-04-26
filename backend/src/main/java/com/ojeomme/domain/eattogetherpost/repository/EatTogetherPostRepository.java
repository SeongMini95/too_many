package com.ojeomme.domain.eattogetherpost.repository;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EatTogetherPostRepository extends JpaRepository<EatTogetherPost, Long>, EatTogetherPostCustomRepository {
}