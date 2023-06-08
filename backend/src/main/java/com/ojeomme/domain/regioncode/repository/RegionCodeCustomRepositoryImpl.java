package com.ojeomme.domain.regioncode.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.ojeomme.domain.regioncode.QRegionCode.regionCode;

@RequiredArgsConstructor
public class RegionCodeCustomRepositoryImpl implements RegionCodeCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Set<String> getDownCode(String code) {
        int depth = factory
                .select(regionCode.regionDepth)
                .from(regionCode)
                .where(regionCode.code.eq(code))
                .fetchFirst();

        Set<String> regionCodes = new HashSet<>();
        regionCodes.add(code);

        for (int i = depth + 1; i <= 3; i++) {
            regionCodes.addAll(factory
                    .select(regionCode.code)
                    .from(regionCode)
                    .where(
                            regionCode.upCode.code.in(regionCodes),
                            regionCode.regionDepth.eq(i)
                    )
                    .fetch());
        }

        return regionCodes;
    }
}
