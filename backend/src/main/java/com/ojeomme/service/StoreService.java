package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.storelikelog.StoreLikeLog;
import com.ojeomme.domain.storelikelog.StoreLikeLogId;
import com.ojeomme.domain.storelikelog.repository.StoreLikeLogRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.RealTimeStoreRankingResponseDto;
import com.ojeomme.dto.response.store.ReviewImageListResponseDto;
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
    private final UserRepository userRepository;
    private final StoreLikeLogRepository storeLikeLogRepository;

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

    @Transactional
    public boolean likeStore(Long userId, Long storeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ApiException(ApiErrorCode.STORE_NOT_FOUND));

        // 있으면 삭제, 없으면 저장
        StoreLikeLog storeLikeLog = storeLikeLogRepository.findById(new StoreLikeLogId(storeId, userId)).orElse(null);
        if (storeLikeLog == null) {
            storeLikeLogRepository.save(StoreLikeLog.builder()
                    .store(store)
                    .user(user)
                    .build());
            store.like();

            return true;
        } else {
            storeLikeLogRepository.delete(storeLikeLog);
            store.cancelLike();

            return false;
        }
    }

    @Transactional(readOnly = true)
    public boolean getStoreLikeLogOfUser(Long userId, Long storeId) {
        return storeLikeLogRepository.existsByUserIdAndStoreId(userId, storeId);
    }

    @Transactional(readOnly = true)
    public ReviewImageListResponseDto getReviewImageList(Long storeId, Long reviewImageId) {
        return reviewImageRepository.getReviewImageList(storeId, reviewImageId);
    }

    @Transactional(readOnly = true)
    public RealTimeStoreRankingResponseDto getTodayStoreRanking(String regionCode) {
        return storeRepository.getRealTimeStoreRanking(regionCode);
    }
}
