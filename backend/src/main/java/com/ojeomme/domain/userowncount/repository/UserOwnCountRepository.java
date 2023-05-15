package com.ojeomme.domain.userowncount.repository;

import com.ojeomme.domain.userowncount.UserOwnCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOwnCountRepository extends JpaRepository<UserOwnCount, Long> {
}