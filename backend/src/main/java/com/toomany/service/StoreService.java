package com.toomany.service;

import com.toomany.common.maps.client.KakaoMapsClient;
import com.toomany.common.maps.entity.PlaceList;
import com.toomany.domain.store.Store;
import com.toomany.domain.store.repository.StoreRepository;
import com.toomany.dto.request.store.SearchStoreListRequestDto;
import com.toomany.dto.response.store.SearchStoreListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final KakaoMapsClient kakaoMapsClient;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public SearchStoreListResponseDto searchStoreList(SearchStoreListRequestDto requestDto) {
        PlaceList placeList = kakaoMapsClient.getPlaceList(requestDto, false);
        List<Store> stores = storeRepository.findAllByKakaoPlaceIdIn(placeList.getKakaoPlaceIds());

        return new SearchStoreListResponseDto(placeList, stores);
    }
}
