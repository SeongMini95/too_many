package com.ojeomme.domain.storelikelog.repository;

import com.ojeomme.domain.storelikelog.StoreLikeLog;
import com.ojeomme.domain.storelikelog.StoreLikeLogId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreLikeLogRepository extends JpaRepository<StoreLikeLog, StoreLikeLogId> {

    boolean existsByUserIdAndStoreId(Long userId, Long storeId);
}
