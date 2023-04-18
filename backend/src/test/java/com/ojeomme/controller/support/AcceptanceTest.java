package com.ojeomme.controller.support;

import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.review.repository.ReviewRepository;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewimage.repository.ReviewImageRepository;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
import com.ojeomme.domain.reviewrecommend.repository.ReviewRecommendRepository;
import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.store.repository.StoreRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.enums.OauthProvider;
import com.ojeomme.domain.user.repository.UserRepository;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AcceptanceTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenProvider authTokenProvider;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RegionCodeRepository regionCodeRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Autowired
    private ReviewRecommendRepository reviewRecommendRepository;

    protected User user;
    protected String accessToken;
    protected String notExistAccessToken;
    protected Store store;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        user = userRepository.save(User.builder()
                .oauthId(RandomStringUtils.randomNumeric(6))
                .oauthProvider(OauthProvider.NAVER)
                .email("test123@naver.com")
                .nickname("test123")
                .profile("")
                .build());

        accessToken = authTokenProvider.createAuthToken(user.getId()).getToken();
        notExistAccessToken = authTokenProvider.createAuthToken(-1L).getToken();

        store = createStore();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    private Store createStore() {
        Category category = categoryRepository.save(Category.builder()
                .categoryName("초밥,롤")
                .categoryDepth(1)
                .build());

        Store store = Store.builder()
                .kakaoPlaceId(1315083198L)
                .category(category)
                .regionCode(regionCodeRepository.findById("1111012200").orElseThrow())
                .storeName("스시소라 광화문점")
                .addressName("서울 종로구 청진동 146")
                .roadAddressName("서울 종로구 종로 19")
                .x("127.03662909986537")
                .y("37.52186058560857")
                .likeCnt(5)
                .build();
        store = storeRepository.save(store);

        Review review1 = Review.builder()
                .user(user)
                .store(store)
                .starScore(4)
                .content("리뷰1")
                .revisitYn(false)
                .build();
        Review review2 = Review.builder()
                .user(user)
                .store(store)
                .starScore(5)
                .content("리뷰2")
                .revisitYn(true)
                .build();

        review1 = reviewRepository.save(review1);
        review2 = reviewRepository.save(review2);

        Set<ReviewImage> reviewImages1 = new LinkedHashSet<>(List.of(
                ReviewImage.builder().review(review1).imageUrl("http://localhost:4000/image1").build(),
                ReviewImage.builder().review(review1).imageUrl("http://localhost:4000/image2").build()
        ));
        Set<ReviewRecommend> reviewRecommends1 = new LinkedHashSet<>(List.of(
                ReviewRecommend.builder().review(review1).recommendType(RecommendType.TASTE).build()
        ));
        Set<ReviewImage> reviewImages2 = Set.of();
        Set<ReviewRecommend> reviewRecommends2 = Set.of();

        reviewImages1 = new LinkedHashSet<>(reviewImageRepository.saveAll(reviewImages1));
        reviewImages2 = new LinkedHashSet<>(reviewImageRepository.saveAll(reviewImages2));
        reviewRecommends1 = new LinkedHashSet<>(reviewRecommendRepository.saveAll(reviewRecommends1));
        reviewRecommends2 = new LinkedHashSet<>(reviewRecommendRepository.saveAll(reviewRecommends2));

        review1.addImages(reviewImages1);
        review1.addRecommends(reviewRecommends1);
        review2.addImages(reviewImages2);
        review2.addRecommends(reviewRecommends2);

        store.writeReview(review2);
        store.writeReview(review1);

        return store;
    }
}
