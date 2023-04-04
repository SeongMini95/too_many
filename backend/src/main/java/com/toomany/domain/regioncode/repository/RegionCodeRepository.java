package com.toomany.domain.regioncode.repository;

import com.toomany.domain.regioncode.RegionCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionCodeRepository extends JpaRepository<RegionCode, String> {
}