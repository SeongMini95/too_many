package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.KakaoPlaceInfo;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class KakaoPlaceClient {

    private final String uri;
    private final WebClient placeClient;

    public KakaoPlaceClient(@Value("${oauth.kakao.uri.place}") String uri, WebClient webClient) {
        this.uri = uri;
        this.placeClient = getPlaceClient(webClient);
    }

    public KakaoPlaceInfo getKakaoPlaceInfo(Long placeId) {
        KakaoPlaceInfo kakaoPlaceInfo = placeClient.get()
                .uri(uriBuilder -> uriBuilder
                        .build(placeId))
                .headers(header -> {
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<KakaoPlaceInfo>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.KAKAO_NOT_EXIST_PLACE));

        validate(kakaoPlaceInfo);

        return kakaoPlaceInfo;
    }

    private void validate(KakaoPlaceInfo kakaoPlaceInfo) {
        if (!kakaoPlaceInfo.getIsExist()) {
            throw new ApiException(ApiErrorCode.KAKAO_NOT_EXIST_PLACE);
        }
    }

    private WebClient getPlaceClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
