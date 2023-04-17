package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.SearchPlaceListResponseDto;
import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto;
import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto.StoreResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final KakaoKeywordClient kakaoKeywordClient;
    private final StoreRepository storeRepository;
    private final ReviewImageRepository reviewImageRepository;

    private static final int SIZE = 5;

    @Transactional(readOnly = true)
    public SearchPlaceListResponseDto searchPlaceList(SearchPlaceListRequestDto requestDto) {
        KakaoPlaceList kakaoPlaceList = kakaoKeywordClient.getKakaoPlaceList(requestDto, false);
        List<Store> stores = storeRepository.findAllByKakaoPlaceIdIn(kakaoPlaceList.getKakaoPlaceIds());

        return new SearchPlaceListResponseDto(kakaoPlaceList, stores);
    }

    @Transactional(readOnly = true)
    public StorePreviewImagesResponseDto getStore(Long storeId) {
        StoreResponseDto store = storeRepository.getStore(storeId).orElseThrow(() -> new ApiException(ApiErrorCode.STORE_NOT_FOUND));
        List<String> previewImages = reviewImageRepository.getPreviewImageList(storeId, PageRequest.of(0, SIZE));

        return new StorePreviewImagesResponseDto(store, previewImages);
    }
}
