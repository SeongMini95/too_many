package com.ojeomme.common.maps.client;

import com.ojeomme.common.maps.entity.KakaoRegionCode;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Component
public class KakaoRegionCodeClient {

    private final String restId;
    private final String uri;
    private final WebClient regionCodeClient;

    public KakaoRegionCodeClient(@Value("${oauth.kakao.rest-id}") String restId,
                                 @Value("${oauth.kakao.uri.region-code}") String uri,
                                 WebClient webClient) {
        this.restId = restId;
        this.uri = uri;
        this.regionCodeClient = getRegionCodeClient(webClient);
    }

    public KakaoRegionCode getRegionCode(String x, String y) {
        KakaoRegionCode kakaoRegionCode = regionCodeClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        }).map(body -> {
                            Integer code = (Integer) body.get("code");
                            if (code != null) {
                                return new ApiException(ApiErrorCode.KAKAO_SEARCH_REGION_CODE, (String) body.get("msg"));
                            } else {
                                return new ApiException(ApiErrorCode.KAKAO_SEARCH_REGION_CODE, (String) body.get("message"));
                            }
                        }))
                .bodyToMono(new ParameterizedTypeReference<KakaoRegionCode>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.KAKAO_SEARCH_REGION_CODE));

        validate(kakaoRegionCode);

        return kakaoRegionCode;
    }

    private void validate(KakaoRegionCode kakaoRegionCode) {
        if (!kakaoRegionCode.exist()) {
            throw new ApiException(ApiErrorCode.KAKAO_NOT_EXIST_REGION_CODE);
        }
    }

    private WebClient getRegionCodeClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(uri)
                .defaultHeaders(header -> {
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                    header.set("Authorization", "KakaoAK " + restId);
                }).build();
    }
}
