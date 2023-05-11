package com.ojeomme.domain.reviewimage.repository;

import com.ojeomme.domain.reviewimage.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long>, ReviewImageCustomRepository {

    Optional<ReviewImage> findTopByReviewId(Long reviewId);
}