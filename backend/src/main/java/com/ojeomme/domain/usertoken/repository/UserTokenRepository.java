package com.ojeomme.domain.usertoken.repository;

import com.ojeomme.domain.usertoken.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    List<UserToken> findByUserId(Long userId);

    Optional<UserToken> findByUserIdAndRefreshTokenAndExpireDatetimeAfter(Long userId, String refreshToken, LocalDateTime now);
}