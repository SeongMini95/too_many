package com.toomany.domain.reviewrecommend.enums;

import com.toomany.common.enums.EnumCodeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RecommendType implements EnumCodeType {

    TASTE("맛", "1"),
    VALUE_FOR_MONEY("가성비", "2"),
    KIND("친절", "3"),
    MOOD("분위기", "4"),
    PARKING("주차", "5");

    private final String desc;
    private final String code;
}
