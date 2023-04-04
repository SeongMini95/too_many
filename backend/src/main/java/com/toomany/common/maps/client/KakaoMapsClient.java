package com.toomany.common.maps.client;

import com.toomany.common.maps.entity.PlaceList;
import com.toomany.dto.request.store.SearchStoreRequestDto;
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
public class KakaoMapsClient {

    private final String restId;
    private final String uri;
    private final WebClient mapsClient;

    public KakaoMapsClient(@Value("${maps.kakao.rest-id}") String restId,
                           @Value("${maps.kakao.uri}") String uri,
                           WebClient webClient) {
        this.restId = restId;
        this.uri = uri;
        this.mapsClient = getMapsClient(webClient);
    }

    public PlaceList getPlace(SearchStoreRequestDto requestDto) {
        return mapsClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", requestDto.getQuery())
                        .queryParam("category_group_code", "FD6")
                        .queryParam("x", requestDto.getX())
                        .queryParam("y", requestDto.getY())
                        .queryParam("page", requestDto.getPage())
                        .build())
                .headers(header -> {
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                    header.set("KakaoAK", restId);
                })
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        }).map(body -> new ApiException(ApiErrorCode.SEARCH_MAPS, body.get("message").toString())))
                .bodyToMono(new ParameterizedTypeReference<PlaceList>() {
                })
                .blockOptional()
                .orElseThrow(() -> new ApiException(ApiErrorCode.SEARCH_MAPS));
    }

    private WebClient getMapsClient(WebClient webClient) {
        return webClient.mutate()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
