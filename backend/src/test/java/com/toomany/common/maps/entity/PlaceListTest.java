package com.toomany.common.maps.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlaceListTest {

    private static final String CATEGORY_NAME = "음식점 > 일식 > 초밥,롤";
    private static final String[] CATEGORY_NAMES = CATEGORY_NAME.split(" > ");
    private static final PlaceList NOT_EXIST_NAME_PLACE_LIST = PlaceList.builder()
            .documents(List.of(
                    PlaceList.Document.builder()
                            .categoryName(CATEGORY_NAME)
                            .build()
            ))
            .build();
    private static final PlaceList EXIST_NAME_PLACE_LIST = PlaceList.builder()
            .categoryNames(CATEGORY_NAMES)
            .documents(List.of(
                    PlaceList.Document.builder()
                            .categoryName(CATEGORY_NAME)
                            .build()
            ))
            .build();

    @Nested
    class getKakaoPlaceIds {

        @Test
        void 매장_id를_가져온다() {
            // given
            PlaceList placeList = PlaceList.builder()
                    .documents(List.of(
                            PlaceList.Document.builder().id("1").build(),
                            PlaceList.Document.builder().id("2").build()
                    ))
                    .build();

            // when
            List<Long> placeIds = placeList.getKakaoPlaceIds();

            // then
            assertThat(placeIds.size()).isEqualTo(2);
            assertThat(placeIds.get(0)).isEqualTo(1);
            assertThat(placeIds.get(1)).isEqualTo(2);
        }
    }

    @Nested
    class exist {

        @Test
        void 데이터가_존재한다() {
            // given
            PlaceList placeList = PlaceList.builder()
                    .meta(PlaceList.Meta.builder()
                            .totalCount(2)
                            .build())
                    .build();

            // when
            boolean exist = placeList.exist();

            // then
            assertThat(exist).isTrue();
        }

        @Test
        void 데이터_없다() {
            // given
            PlaceList placeList = PlaceList.builder()
                    .meta(PlaceList.Meta.builder()
                            .totalCount(0)
                            .build())
                    .build();

            // when
            boolean exist = placeList.exist();

            // then
            assertThat(exist).isFalse();
        }
    }

    @Nested
    class getDepth {

        @Test
        void depth를_가져온다_categoryNames이_null() {
            // given

            // when
            int depth = NOT_EXIST_NAME_PLACE_LIST.getDepth();

            // then
            assertThat(depth).isEqualTo(2);
        }

        @Test
        void depth를_가져온다() {
            // given

            // when
            int depth = EXIST_NAME_PLACE_LIST.getDepth();

            // then
            assertThat(depth).isEqualTo(2);
        }
    }

    @Nested
    class getLastCategoryName {

        @Test
        void 카테고리_이름을_가져온다_categoryNames이_null() {
            // given

            // when
            String lastCategoryName = NOT_EXIST_NAME_PLACE_LIST.getLastCategoryName();

            // then
            assertThat(lastCategoryName).isEqualTo("초밥,롤");
        }

        @Test
        void 카테고리_이름을_가져온다() {
            // given

            // when
            String lastCategoryName = EXIST_NAME_PLACE_LIST.getLastCategoryName();

            // then
            assertThat(lastCategoryName).isEqualTo("초밥,롤");
        }
    }
}