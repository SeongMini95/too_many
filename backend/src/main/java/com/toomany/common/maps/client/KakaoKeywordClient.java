package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.KakaoPlaceList;
import com.toomany.dto.request.store.SearchPlaceListRequestDto;
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
public class KakaoKeywordClient {

    private final String restId;
    private final String uri;
    private final WebClient mapsClient;

    public KakaoKeywordClient(@Value("${oauth.kakao.rest-id}") String restId,
                              @Value("${oauth.kakao.uri.keyword}") String uri,
                              WebClient webClient) {
        this.restId = restId;
        this.uri = uri;
        this.mapsClient = getMapsClient(webClient);
    }

    public KakaoPlaceList getKakaoPlaceList(SearchPlaceListRequestDto requestDto, boolean register) {
        return mapsClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .queryParam("query", requestDto.getQuery())
                            .queryParam("category_group_code", "FD6")
                            .queryParam("x", requestDto.getX())
                            .queryParam("y", requestDto.getY())
                            .queryParam("page", requestDto.getPage());

                    if (register) {
                        uriBuilder
                                .queryParam("radius", 0)
                                .queryParam("size", 1)
                                .queryParam("sort", "distance");
                    }

                    return uriBuilder.build();
                })
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
                                return new ApiException(ApiErrorCode.KAKAO_SEARCH_PLACE, (String) body.get("msg"));
                            } else {
                                return new ApiException(ApiErrorCode.KAKAO_SEARCH_PLACE, (String) body.get("message"));
                            }
                        }))
                .bodyToMono(new ParameterizedTypeReference<KakaoPlaceList>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.KAKAO_SEARCH_PLACE));
    }

    private WebClient getMapsClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
