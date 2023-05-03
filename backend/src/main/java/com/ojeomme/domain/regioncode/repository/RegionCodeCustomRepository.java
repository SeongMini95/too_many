package com.ojeomme.domain.regioncode.repository;

import java.util.Set;

public interface RegionCodeCustomRepository {

    Set<String> getDownCode(String code);
}
