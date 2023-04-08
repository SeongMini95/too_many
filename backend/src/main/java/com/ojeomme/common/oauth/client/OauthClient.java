package com.ojeomme.common.oauth.client;

import com.ojeomme.common.oauth.info.OauthUserInfo;
import com.ojeomme.domain.user.enums.OauthProvider;

public interface OauthClient {

    boolean support(OauthProvider oauthProvider);

    String getLoginUri(String redirectUri);

    OauthUserInfo getUserInfo(String redirectUri, String code);
}
