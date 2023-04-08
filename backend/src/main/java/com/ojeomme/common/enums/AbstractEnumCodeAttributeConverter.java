package com.ojeomme.common.enums;

import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.lang.reflect.ParameterizedType;

@Converter
public class AbstractEnumCodeAttributeConverter<E extends Enum<E> & EnumCodeType> implements AttributeConverter<E, String> {

    private final Class<E> targetEnumClass;
    private final boolean nullable;
    private final String enumName;

    public AbstractEnumCodeAttributeConverter(boolean nullable, String enumName) {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.targetEnumClass = (Class<E>) type.getActualTypeArguments()[0];
        this.nullable = nullable;
        this.enumName = enumName;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (!nullable && attribute == null) {
            throw new ApiException(ApiErrorCode.ENUM_CODE, String.format("%s(은)는 Null로 저장할 수 없습니다.", enumName));
        }

        return EnumCodeConverterUtils.toCode(attribute);
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if (!nullable && StringUtils.isBlank(dbData)) {
            throw new ApiException(ApiErrorCode.ENUM_CODE, String.format("%s(이)가 DB에 Null 혹은 Empty로 저장되어 있습니다.", enumName));
        }

        return EnumCodeConverterUtils.ofCode(dbData, targetEnumClass);
    }
}
