package com.toomany.common.oauth.client;

import com.toomany.common.oauth.info.NaverOauthUserInfo;
import com.toomany.common.oauth.info.OauthUserInfo;
import com.toomany.domain.user.enums.OauthProvider;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Component
public class NaverOauthClient implements OauthClient {

    private final String clientId;
    private final String secretId;
    private final String authorizationUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final WebClient tokenClient;
    private final WebClient userInfoClient;

    public NaverOauthClient(@Value("${oauth.naver.client-id}") String clientId,
                            @Value("${oauth.naver.secret-id}") String secretId,
                            @Value("${oauth.naver.uri.authorization}") String authorizationUri,
                            @Value("${oauth.naver.uri.token}") String tokenUri,
                            @Value("${oauth.naver.uri.user-info}") String userInfoUri,
                            WebClient webClient) {
        this.clientId = clientId;
        this.secretId = secretId;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.tokenClient = getTokenClient(webClient);
        this.userInfoClient = getUserInfoClient(webClient);
    }

    @Override
    public boolean support(OauthProvider oauthProvider) {
        return OauthProvider.NAVER.equals(oauthProvider);
    }

    @Override
    public String getLoginUri(String redirectUri) {
        return UriComponentsBuilder.fromHttpUrl(authorizationUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", "")
                .toUriString();
    }

    @Override
    public OauthUserInfo getUserInfo(String redirectUri, String code) {
        String token = getToken(redirectUri, code);
        return getOauthUserInfo(token);
    }

    private String getToken(String redirectUri, String code) {
        Map<String, Object> response = tokenClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", secretId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .queryParam("state", "")
                        .build())
                .headers(header -> {
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.SOCIAL_LOGIN));

        validateResponse(response);

        return response.get("access_token").toString();
    }

    private void validateResponse(Map<String, Object> response) {
        if (!response.containsKey("access_token")) {
            throw new ApiException(ApiErrorCode.SOCIAL_LOGIN, response.get("error_description").toString());
        }
    }

    private OauthUserInfo getOauthUserInfo(String token) {
        Map<String, Object> response = userInfoClient.get()
                .headers(header -> header.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.SOCIAL_LOGIN));

        return new NaverOauthUserInfo(response);
    }

    private WebClient getTokenClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(tokenUri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient getUserInfoClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(userInfoUri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
