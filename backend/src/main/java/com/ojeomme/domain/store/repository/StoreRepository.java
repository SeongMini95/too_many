package com.ojeomme.domain.store.repository;

import com.ojeomme.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreCustomRepository {

    List<Store> findAllByKakaoPlaceIdIn(List<Long> kakaoPlaceIds);

    Optional<Store> findByKakaoPlaceId(Long kakaoPlaceId);
}