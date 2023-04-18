package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoKeywordClient;
import com.ojeomme.common.maps.client.KakaoPlaceClient;
import com.ojeomme.common.maps.client.KakaoRegionCodeClient;
import com.ojeomme.common.maps.entity.KakaoPlaceInfo;
import com.ojeomme.common.maps.entity.KakaoPlaceList;
import com.ojeomme.common.maps.entity.KakaoRegionCode;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.review.repository.ReviewRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.review.WriteReviewRequestDto;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.dto.response.review.WriteReviewResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final RegionCodeRepository regionCodeRepository;
    private final KakaoPlaceClient kakaoPlaceClient;
    private final KakaoKeywordClient kakaoKeywordClient;
    private final KakaoRegionCodeClient kakaoRegionCodeClient;
    private final ImageService imageService;

    @Transactional
    public WriteReviewResponseDto writeReview(Long userId, Long placeId, WriteReviewRequestDto requestDto) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        KakaoPlaceInfo kakaoPlaceInfo = kakaoPlaceClient.getKakaoPlaceInfo(placeId);
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
                .x(kakaoPlaceList.getX())
                .y(kakaoPlaceList.getY())
                .build();
        if (store != null) {
            store.updateStoreInfo(saveStore);
        } else {
            store = storeRepository.save(saveStore);
        }

        // 리뷰 작성
        List<String> images = new ArrayList<>();
        for (String v : requestDto.getImages()) {
            String url = imageService.copyImage(v);
            images.add(url);
        }

        Review saveReview = reviewRepository.save(requestDto.toReview(user, store, images));
        store.writeReview(saveReview);

        return new WriteReviewResponseDto(store.getId(), saveReview);
    }

    @Transactional(readOnly = true)
    public ReviewListResponseDto getReviewList(Long storeId, Long reviewId) {
        return reviewRepository.getReviewList(storeId, reviewId);
    }
}
