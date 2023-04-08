package com.ojeomme.domain.reviewrecommend.enums.converter;

import com.ojeomme.common.enums.AbstractEnumCodeAttributeConverter;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;

public class RecommendTypeConverter extends AbstractEnumCodeAttributeConverter<RecommendType> {

    private static final String ENUM_NAME = "추천 포인트";

    public RecommendTypeConverter() {
        super(false, ENUM_NAME);
    }
}
