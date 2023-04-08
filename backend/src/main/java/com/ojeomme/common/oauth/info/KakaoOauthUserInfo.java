package com.ojeomme.common.oauth.info;

import java.util.Map;

public class KakaoOauthUserInfo extends OauthUserInfo {

    public KakaoOauthUserInfo(Map<String, Object> response) {
        super(response);
    }

    @Override
    public String getId() {
        return info.get("id").toString();
    }

    @Override
    public String getEmail() {
        return ((Map<String, Object>) info.get("kakao_account")).get("email").toString();
    }

    @Override
    public String getNickname() {
        return ((Map<String, Object>) ((Map<String, Object>) info.get("kakao_account")).get("profile")).get("nickname").toString();
    }
}
