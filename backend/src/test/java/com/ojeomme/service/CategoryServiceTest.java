package com.ojeomme.service;

import com.ojeomme.domain.category.Category;
import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.dto.response.category.CategoryListResponseDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    class getCategoryList {

        @Test
        void 카테고리를_가져온다() {
            // given
            List<Category> categories = List.of(
                    Category.builder().id(1L).categoryName("한식").build(),
                    Category.builder().id(2L).categoryName("일식").build(),
                    Category.builder().id(3L).categoryName("중식").build()
            );
            given(categoryRepository.findByUpCategoryIdAndCategoryDepth(anyLong(), anyInt())).willReturn(categories);

            // when
            CategoryListResponseDto responseDto = categoryService.getCategoryList(1L, 1);

            // then
            assertThat(responseDto.getCategories()).hasSameSizeAs(categories);
            for (int i = 0; i < responseDto.getCategories().size(); i++) {
                assertThat(responseDto.getCategories().get(i).getCategoryId()).isEqualTo(categories.get(i).getId());
                assertThat(responseDto.getCategories().get(i).getCategoryName()).isEqualTo(categories.get(i).getCategoryName());
            }
        }
    }
}