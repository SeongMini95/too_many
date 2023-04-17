package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.SearchPlaceListResponseDto;
import com.ojeomme.dto.response.store.StoreReviewsResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final KakaoKeywordClient kakaoKeywordClient;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public SearchPlaceListResponseDto searchPlaceList(SearchPlaceListRequestDto requestDto) {
        KakaoPlaceList kakaoPlaceList = kakaoKeywordClient.getKakaoPlaceList(requestDto, false);
        List<Store> stores = storeRepository.findAllByKakaoPlaceIdIn(kakaoPlaceList.getKakaoPlaceIds());

        return new SearchPlaceListResponseDto(kakaoPlaceList, stores);
    }

    @Transactional(readOnly = true)
    public StoreReviewsResponseDto getStoreReviews(Long storeId) {
        return storeRepository.getStoreReview(storeId).orElseThrow(() -> new ApiException(ApiErrorCode.STORE_NOT_FOUND));
    }
}
