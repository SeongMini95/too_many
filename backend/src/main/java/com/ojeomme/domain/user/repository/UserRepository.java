package com.ojeomme.domain.user.repository;

import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByOauthIdAndOauthProvider(String oauthId, OauthProvider oauthProvider);
}