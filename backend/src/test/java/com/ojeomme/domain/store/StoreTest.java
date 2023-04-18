package com.ojeomme.domain.store;

import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.review.Review;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StoreTest {

    @Nested
    class updateStoreInfo {

        @Test
        void 매장_정보를_업데이트_한다() {
            // given
            Store store = Store.builder().build();
            Store newStore = Store.builder()
                    .category(Category.builder()
                            .categoryDepth(3)
                            .categoryName("초밥,롤")
                            .build())
                    .regionCode(RegionCode.builder()
                            .code("1100000000")
                            .regionDepth(1)
                            .regionName("서울")
                            .build())
                    .storeName("스시코우지")
                    .addressName("구주소")
                    .roadAddressName("신주소")
                    .x("123")
                    .y("321")
                    .build();

            // when
            store.updateStoreInfo(newStore);

            // then
            assertThat(store.getCategory().getCategoryDepth()).isEqualTo(newStore.getCategory().getCategoryDepth());
            assertThat(store.getCategory().getCategoryName()).isEqualTo(newStore.getCategory().getCategoryName());

            assertThat(store.getRegionCode().getRegionDepth()).isEqualTo(newStore.getRegionCode().getRegionDepth());
            assertThat(store.getRegionCode().getRegionName()).isEqualTo(newStore.getRegionCode().getRegionName());

            assertThat(store.getStoreName()).isEqualTo(newStore.getStoreName());
            assertThat(store.getRoadAddressName()).isEqualTo(newStore.getRoadAddressName());
            assertThat(store.getAddressName()).isEqualTo(newStore.getAddressName());
            assertThat(store.getX()).isEqualTo(newStore.getX());
            assertThat(store.getY()).isEqualTo(newStore.getY());
        }
    }

    @Nested
    class writeReview {

        @Test
        void 리뷰를_작성한다() {
            // given
            Store store = new Store();

            Review review = Review.builder()
                    .revisitYn(true)
                    .content("리뷰 작성")
                    .build();

            // when
            store.writeReview(review);

            // then
            assertThat(store.getReviews().get(0).isRevisitYn()).isTrue();
            assertThat(store.getReviews().get(0).getContent()).isEqualTo(review.getContent());
        }
    }
}