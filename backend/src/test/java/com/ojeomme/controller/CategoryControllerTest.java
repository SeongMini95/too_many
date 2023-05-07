package com.ojeomme.controller;

import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryControllerTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    class getCategoryList {

        @Test
        void 카테고리를_가져온다() {
            // given
            Category category1 = createCategory(null, "한식", 1);
            Category category2 = createCategory(null, "일식", 1);
            Category category3 = createCategory(null, "중식", 1);

            List<Category> categories = List.of(category1, category2, category3);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .param("depth", 1)
                    .when().get("/api/category/list")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("categories")).hasSameSizeAs(categories);
            for (int i = 0; i < jsonPath.getList("categories").size(); i++) {
                assertThat(jsonPath.getLong("categories[" + i + "].categoryId")).isEqualTo(categories.get(i).getId());
                assertThat(jsonPath.getString("categories[" + i + "].categoryName")).isEqualTo(categories.get(i).getCategoryName());
            }
        }

        private Category createCategory(Category upCategory, String name, int depth) {
            return categoryRepository.save(Category.builder()
                    .upCategory(upCategory)
                    .categoryName(name)
                    .categoryDepth(depth)
                    .build());
        }
    }
}