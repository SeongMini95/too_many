package com.toomany.service;

import com.toomany.common.maps.client.KakaoKeywordClient;
import com.toomany.common.maps.client.KakaoPlaceClient;
import com.toomany.common.maps.client.KakaoRegionCodeClient;
import com.toomany.common.maps.entity.KakaoPlaceInfo;
import com.toomany.common.maps.entity.KakaoPlaceList;
import com.toomany.common.maps.entity.KakaoRegionCode;
import com.toomany.domain.category.Category;
import com.toomany.domain.category.repository.CategoryRepository;
import com.toomany.domain.regioncode.RegionCode;
import com.toomany.domain.regioncode.repository.RegionCodeRepository;
import com.toomany.domain.store.Store;
import com.toomany.domain.store.repository.StoreRepository;
import com.toomany.domain.user.User;
import com.toomany.domain.user.repository.UserRepository;
import com.toomany.dto.request.review.WriteReviewRequestDto;
import com.toomany.dto.request.store.SearchPlaceListRequestDto;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final RegionCodeRepository regionCodeRepository;
    private final KakaoPlaceClient kakaoPlaceClient;
    private final KakaoKeywordClient kakaoKeywordClient;
    private final KakaoRegionCodeClient kakaoRegionCodeClient;

    @Transactional
    public Long writeReview(Long userId, WriteReviewRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        KakaoPlaceInfo kakaoPlaceInfo = kakaoPlaceClient.getKakaoPlaceInfo(requestDto.getPlaceId());
        KakaoPlaceList kakaoPlaceList = kakaoKeywordClient.getKakaoPlaceList(SearchPlaceListRequestDto.builder()
                .query(kakaoPlaceInfo.getPlaceName())
                .x(requestDto.getX())
                .y(requestDto.getY())
                .page(1)
                .build(), true);

        // 매장을 찾을 수 없으면 exception
        if (!kakaoPlaceList.exist()) {
            throw new ApiException(ApiErrorCode.KAKAO_NOT_EXIST_PLACE);
        }

        // 카테고리 저장 및 가져오기
        int categoryDepth = kakaoPlaceList.getDepth();
        String lastCategoryName = kakaoPlaceList.getLastCategoryName();
        Category category = categoryRepository.findByCategoryDepthAndCategoryName(categoryDepth, lastCategoryName).orElse(null);
        if (category == null) { // 카테고리가 없으면 저장
            String[] categoryNames = kakaoPlaceList.getCategoryNames();
            Category upCategory = null;

            for (int i = 1; i < categoryNames.length; i++) {
                if (i != categoryDepth) {
                    category = categoryRepository.findByCategoryDepthAndCategoryName(i, categoryNames[i]).orElse(null);
                } else {
                    category = null;
                }

                if (category == null) {
                    upCategory = Category.builder()
                            .upCategory(upCategory)
                            .categoryDepth(i)
                            .categoryName(categoryNames[i])
                            .build();

                    category = categoryRepository.save(upCategory);
                }
            }
        }

        // 지역코드 가져오기
        KakaoRegionCode kakaoRegionCode = kakaoRegionCodeClient.getRegionCode(requestDto.getX(), requestDto.getY());
        RegionCode regionCode = regionCodeRepository.findById(kakaoRegionCode.getCode()).orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));

        // 매장이 있으면 update 없으면 저장
        Store store = storeRepository.findByKakaoPlaceId(kakaoPlaceInfo.getPlaceId()).orElse(null);
        Store saveStore = Store.builder()
                .kakaoPlaceId(kakaoPlaceInfo.getPlaceId())
                .category(category)
                .regionCode(regionCode)
                .storeName(kakaoPlaceInfo.getPlaceName())
                .addressName(kakaoPlaceInfo.getAddress())
                .roadAddressName(kakaoPlaceInfo.getRoadAddress())
                .x(kakaoPlaceInfo.getX())
                .y(kakaoPlaceInfo.getY())
                .build();
        if (store != null) {
            store.updateStoreInfo(saveStore);
        } else {
            store = storeRepository.save(saveStore);
        }

        // 리뷰 작성
        store.writeReview(requestDto.toReview(user, store));

        return store.getId();
    }
}
