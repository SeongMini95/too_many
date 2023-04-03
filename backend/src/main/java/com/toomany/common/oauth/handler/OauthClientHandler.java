package com.toomany.common.oauth.handler;

import com.toomany.common.oauth.client.OauthClient;
import com.toomany.common.oauth.info.OauthUserInfo;
import com.toomany.domain.user.enums.OauthProvider;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OauthClientHandler {

    private final List<OauthClient> oauthClients;

    public String getLoginUri(OauthProvider oauthProvider, String redirectUri) {
        OauthClient client = getOauthClient(oauthProvider);
        return client.getLoginUri(redirectUri);
    }

    public OauthUserInfo getOauthUserInfo(OauthProvider oauthProvider, String redirectUri, String code) {
        OauthClient client = getOauthClient(oauthProvider);
        return client.getUserInfo(redirectUri, code);
    }

    private OauthClient getOauthClient(OauthProvider oauthProvider) {
        return oauthClients.stream()
                .filter(client -> client.support(oauthProvider))
                .findFirst()
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_SUPPORT_OAUTH_PROVIDER));
    }
}
