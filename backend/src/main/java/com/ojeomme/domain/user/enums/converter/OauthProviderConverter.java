package com.ojeomme.domain.user.enums.converter;

import com.ojeomme.common.enums.AbstractEnumCodeAttributeConverter;
import com.ojeomme.domain.user.enums.OauthProvider;

public class OauthProviderConverter extends AbstractEnumCodeAttributeConverter<OauthProvider> {

    private static final String ENUM_NAME = "플랫폼 제공자";

    public OauthProviderConverter() {
        super(false, ENUM_NAME);
    }
}
