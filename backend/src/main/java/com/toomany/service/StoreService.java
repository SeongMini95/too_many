package com.toomany.service;

import com.toomany.common.maps.client.KakaoKeywordClient;
import com.toomany.common.maps.entity.KakaoPlaceList;
import com.toomany.domain.store.Store;
import com.toomany.domain.store.repository.StoreRepository;
import com.toomany.dto.request.store.SearchPlaceListRequestDto;
import com.toomany.dto.response.store.SearchStoreListResponseDto;
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
    public SearchStoreListResponseDto searchStoreList(SearchPlaceListRequestDto requestDto) {
        KakaoPlaceList kakaoPlaceList = kakaoKeywordClient.getKakaoPlaceList(requestDto, false);
        List<Store> stores = storeRepository.findAllByKakaoPlaceIdIn(kakaoPlaceList.getKakaoPlaceIds());

        return new SearchStoreListResponseDto(kakaoPlaceList, stores);
    }
}
