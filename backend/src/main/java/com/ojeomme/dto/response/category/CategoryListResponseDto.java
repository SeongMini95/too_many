package com.ojeomme.dto.response.category;

import com.ojeomme.domain.category.Category;
import lombok.Getter;

import java.util.List;

@Getter
public class CategoryListResponseDto {

    private final List<CategoryResponseDto> categories;

    public CategoryListResponseDto(List<CategoryResponseDto> categories) {
        this.categories = categories;
    }

    @Getter
    public static class CategoryResponseDto {

        private final Long categoryId;
        private final String categoryName;

        public CategoryResponseDto(Category category) {
            this.categoryId = category.getId();
            this.categoryName = category.getCategoryName();
        }
    }
}
