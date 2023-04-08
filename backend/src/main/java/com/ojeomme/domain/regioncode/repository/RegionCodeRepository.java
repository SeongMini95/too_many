package com.ojeomme.domain.regioncode.repository;

import com.ojeomme.domain.regioncode.RegionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionCodeRepository extends JpaRepository<RegionCode, String> {

    List<RegionCode> findAllByRegionDepthNot(int regionDepth);
}