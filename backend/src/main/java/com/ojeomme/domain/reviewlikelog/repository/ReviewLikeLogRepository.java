package com.ojeomme.domain.reviewlikelog.repository;

import com.ojeomme.domain.reviewlikelog.ReviewLikeLog;
import com.ojeomme.domain.reviewlikelog.ReviewLikeLogId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewLikeLogRepository extends JpaRepository<ReviewLikeLog, ReviewLikeLogId> {

    List<ReviewLikeLog> findByUserIdAndReviewStoreId(Long userId, Long storeId);
}
