package com.toomany.domain.store.repository;

import com.toomany.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<Store> findAllByKakaoPlaceIdIn(List<Long> kakaoPlaceIds);

    Optional<Store> findByKakaoPlaceId(Long kakaoPlaceId);
}