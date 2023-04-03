package com.toomany.common.oauth.info;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OauthUserInfo {

    protected final Map<String, Object> info;

    abstract public String getId();

    abstract public String getEmail();

    abstract public String getNickname();
}
