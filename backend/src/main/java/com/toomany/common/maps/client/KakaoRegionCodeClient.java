package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.KakaoRegionCode;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
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
        return regionCodeClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .build())
                .headers(header -> {
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                    header.set("KakaoAK", restId);
                })
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        }).map(body -> {
                            Integer code = (Integer) body.get("code");
                            if (code != null) {
                                return new ApiException(ApiErrorCode.SEARCH_MAPS, (String) body.get("msg"));
                            } else {
                                return new ApiException(ApiErrorCode.SEARCH_MAPS, (String) body.get("message"));
                            }
                        }))
                .bodyToMono(new ParameterizedTypeReference<KakaoRegionCode>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.SEARCH_MAPS));
    }

    private WebClient getRegionCodeClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
