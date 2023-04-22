package com.ojeomme.domain.review.repository;

import com.ojeomme.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {

    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);
}