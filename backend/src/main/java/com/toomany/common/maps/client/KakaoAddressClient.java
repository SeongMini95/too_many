package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.KakaoAddressCoord;
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
public class KakaoAddressClient {

    private final String restId;
    private final String uri;
    private final WebClient addressClient;

    public KakaoAddressClient(@Value("${oauth.kakao.rest-id}") String restId,
                              @Value("${oauth.kakao.uri.address}") String uri,
                              WebClient webClient) {
        this.restId = restId;
        this.uri = uri;
        this.addressClient = getAddressClient(webClient);
    }

    public KakaoAddressCoord getKakaoAddressCoord(String address) {
        KakaoAddressCoord kakaoAddressCoord = addressClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", address)
                        .queryParam("size", 1)
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
                                return new ApiException(ApiErrorCode.KAKAO_SEARCH_ADDRESS, (String) body.get("msg"));
                            } else {
                                return new ApiException(ApiErrorCode.KAKAO_SEARCH_ADDRESS, (String) body.get("message"));
                            }
                        }))
                .bodyToMono(new ParameterizedTypeReference<KakaoAddressCoord>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.KAKAO_SEARCH_ADDRESS));

        validate(kakaoAddressCoord);

        return kakaoAddressCoord;
    }

    private void validate(KakaoAddressCoord kakaoAddressCoord) {
        if (!kakaoAddressCoord.exist()) {
            throw new ApiException(ApiErrorCode.KAKAO_NOE_EXIST_ADDRESS);
        }
    }

    private WebClient getAddressClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
