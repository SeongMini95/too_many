package com.toomany.common.enums;

import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import lombok.experimental.UtilityClass;

import java.util.EnumSet;

@UtilityClass
public class EnumCodeConverterUtils {

    public static <T extends Enum<T> & EnumCodeType> T ofCode(String code, Class<T> enumClass) {
        if (code == null || code.isBlank()) {
            return null;
        }

        return EnumSet.allOf(enumClass).stream()
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new ApiException(ApiErrorCode.ENUM_CODE, String.format("enum=[%s], code=[%s]가 존재하지 않습니다.", enumClass.getName(), code)));
    }

    public static <T extends Enum<T> & EnumCodeType> String toCode(T enumValue) {
        if (enumValue == null) {
            return null;
        }

        return enumValue.getCode();
    }
}
