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
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.reviewlikelog.ReviewLikeLog;
import com.ojeomme.domain.reviewlikelog.ReviewLikeLogId;
import com.ojeomme.domain.reviewlikelog.repository.ReviewLikeLogRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.review.ModifyReviewRequestDto;
import com.ojeomme.dto.request.review.WriteReviewRequestDto;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.review.ReviewListResponseDto;
import com.ojeomme.dto.response.review.ReviewResponseDto;
import com.ojeomme.dto.response.review.WriteReviewResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ReviewLikeLogRepository reviewLikeLogRepository;
    private final ReviewImageRepository reviewImageRepository;

    @Transactional
    public WriteReviewResponseDto writeReview(Long userId, Long placeId, WriteReviewRequestDto requestDto) throws IOException {
        // 1주일에 최대 1개만 작성
        reviewRepository.getWithinAWeek(userId, placeId).ifPresent(v -> {
            LocalDate now = LocalDate.now();
            int dayOfWeek = now.getDayOfWeek().getValue();

            LocalDate mon = now.minusDays(dayOfWeek - 1);
            if (!v.getCreateDatetime().toLocalDate().isBefore(mon)) {
                throw new ApiException(ApiErrorCode.ALREADY_EXIST_REVIEW);
            }
        });

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

        // 메인 이미지가 등록이 안되어있으면
        if (StringUtils.isBlank(store.getMainImageUrl()) && !images.isEmpty()) {
            store.changeMainImage(images.get(0));
        }

        return new WriteReviewResponseDto(store.getId(), saveReview.getId());
    }

    @Transactional(readOnly = true)
    public ReviewListResponseDto getReviewList(Long userId, Long storeId, Long moreId) {
        return reviewRepository.getReviewList(userId, storeId, moreId);
    }

    @Transactional
    public ReviewResponseDto modifyReview(Long userId, Long reviewId, ModifyReviewRequestDto requestDto) throws IOException {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId).orElseThrow(() -> new ApiException(ApiErrorCode.REVIEW_NOT_FOUND));

        List<String> images = new ArrayList<>();
        for (String v : requestDto.getImages()) {
            String url = imageService.copyImage(v);
            images.add(url);
        }
        review.modifyReview(requestDto.toReview(review, images));

        return new ReviewResponseDto(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        reviewRepository.findByIdAndUserId(reviewId, userId).ifPresentOrElse(
                reviewRepository::delete,
                () -> {
                    throw new ApiException(ApiErrorCode.REVIEW_NOT_FOUND);
                });
    }

    @Transactional
    public boolean likeReview(Long userId, Long reviewId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ApiException(ApiErrorCode.REVIEW_NOT_FOUND));

        // 있으면 취소, 없으면 저장
        boolean isLike;
        ReviewLikeLog reviewLikeLog = reviewLikeLogRepository.findById(new ReviewLikeLogId(reviewId, userId)).orElse(null);
        if (reviewLikeLog == null) {
            reviewLikeLogRepository.save(ReviewLikeLog.builder()
                    .review(review)
                    .user(user)
                    .build());
            review.like();
            isLike = true;
        } else {
            reviewLikeLogRepository.delete(reviewLikeLog);
            review.cancelLike();
            isLike = false;
        }

        // 좋아요 수가 제일 많으면 이미지가 존재하면 메인 이미지 변경
        int likeCnt = review.getLikeCnt();
        boolean changeMainImage = !reviewRepository.existsByStoreIdAndLikeCntGreaterThan(review.getStore().getId(), likeCnt);
        if (changeMainImage) {
            reviewImageRepository.findTopByReviewId(reviewId).ifPresent(v -> review.getStore().changeMainImage(v.getImageUrl()));
        }

        return isLike;
    }

    @Transactional(readOnly = true)
    public List<Long> getReviewLikeLogListOfUser(Long userId, Long storeId) {
        return reviewLikeLogRepository.findByUserIdAndReviewStoreId(userId, storeId).stream()
                .map(v -> v.getReview().getId())
                .collect(Collectors.toList());
    }
}
