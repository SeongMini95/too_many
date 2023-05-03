package com.ojeomme.domain.regioncode.repository;

import com.ojeomme.domain.regioncode.QRegionCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class RegionCodeCustomRepositoryImpl implements RegionCodeCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Set<String> getDownCode(String code) {
        int depth = factory
                .select(QRegionCode.regionCode.regionDepth)
                .from(QRegionCode.regionCode)
                .where(QRegionCode.regionCode.code.eq(code))
                .fetchFirst();

        Set<String> regionCodes = new HashSet<>();
        regionCodes.add(code);

        for (int i = depth + 1; i <= 3; i++) {
            regionCodes.addAll(factory
                    .select(QRegionCode.regionCode.code)
                    .from(QRegionCode.regionCode)
                    .where(
                            QRegionCode.regionCode.upCode.code.in(regionCodes),
                            QRegionCode.regionCode.regionDepth.eq(i)
                    )
                    .fetch());
        }
        
        return regionCodes;
    }
}
