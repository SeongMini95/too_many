package com.ojeomme.service;

import com.ojeomme.domain.category.repository.CategoryRepository;
import com.ojeomme.dto.response.category.CategoryListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryListResponseDto getCategoryList(Long upCategory, int depth) {
        return new CategoryListResponseDto(categoryRepository.findByUpCategoryIdAndCategoryDepth(upCategory, depth).stream()
                .map(CategoryListResponseDto.CategoryResponseDto::new)
                .collect(Collectors.toList()));
    }
}
