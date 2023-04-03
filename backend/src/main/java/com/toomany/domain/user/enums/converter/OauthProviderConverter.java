package com.toomany.domain.user.enums.converter;

import com.toomany.common.enums.AbstractEnumCodeAttributeConverter;
import com.toomany.domain.user.enums.OauthProvider;

public class OauthProviderConverter extends AbstractEnumCodeAttributeConverter<OauthProvider> {

    private static final String ENUM_NAME = "플랫폼 제공자";

    public OauthProviderConverter() {
        super(false, ENUM_NAME);
    }
}
