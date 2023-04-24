package com.ojeomme.controller.support;

import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.review.Review;
import com.ojeomme.domain.review.repository.ReviewRepository;
import com.ojeomme.domain.reviewimage.ReviewImage;
import com.ojeomme.domain.reviewrecommend.ReviewRecommend;
import com.ojeomme.domain.reviewrecommend.enums.RecommendType;
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

    protected User user;
    protected String accessToken;
    protected String notExistAccessToken;
    protected Store store;
    protected Review review;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        user = userRepository.save(User.builder()
                .oauthId(RandomStringUtils.randomNumeric(6))
                .oauthProvider(OauthProvider.NAVER)
                .email("test123@naver.com")
                .nickname("test123")
                .profile("http://localhost:4000/profile.png")
                .build());

        accessToken = authTokenProvider.createAuthToken(user.getId()).getToken();
        notExistAccessToken = authTokenProvider.createAuthToken(-1L).getToken();

        createStore();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    private void createStore() {
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

        this.store = storeRepository.save(store);

        Review review = Review.builder()
                .user(user)
                .store(this.store)
                .starScore(4)
                .content("리뷰1")
                .revisitYn(false)
                .build();
        Set<ReviewImage> reviewImages = new LinkedHashSet<>(List.of(
                ReviewImage.builder().review(review).imageUrl("http://localhost:4000/image1.png").build(),
                ReviewImage.builder().review(review).imageUrl("http://localhost:4000/image2.png").build()
        ));
        Set<ReviewRecommend> reviewRecommends = Set.of(
                ReviewRecommend.builder().review(review).recommendType(RecommendType.TASTE).build()
        );

        review.addImages(reviewImages);
        review.addRecommends(reviewRecommends);

        this.review = reviewRepository.save(review);
        store.writeReview(this.review);
    }
}
