package com.toomany.common.oauth.client;

import com.toomany.common.oauth.info.OauthUserInfo;
import com.toomany.domain.user.enums.OauthProvider;

public interface OauthClient {

    boolean support(OauthProvider oauthProvider);

    String getLoginUri(String redirectUri);

    OauthUserInfo getUserInfo(String redirectUri, String code);
}
