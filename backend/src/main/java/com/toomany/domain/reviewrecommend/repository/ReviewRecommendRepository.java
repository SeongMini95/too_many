package com.toomany.domain.reviewrecommend.repository;

import com.toomany.domain.reviewrecommend.ReviewRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRecommendRepository extends JpaRepository<ReviewRecommend, Long> {
}