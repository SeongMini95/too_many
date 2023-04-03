package com.toomany.common.oauth.info;

import java.util.Map;

public class NaverOauthUserInfo extends OauthUserInfo {

    public NaverOauthUserInfo(Map<String, Object> response) {
        super(response);
    }

    @Override
    public String getId() {
        return ((Map<String, Object>) info.get("response")).get("id").toString();
    }

    @Override
    public String getEmail() {
        return ((Map<String, Object>) info.get("response")).get("email").toString();
    }

    @Override
    public String getNickname() {
        return ((Map<String, Object>) info.get("response")).get("nickname").toString();
    }
}
