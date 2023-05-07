package com.ojeomme.controller;

import com.ojeomme.dto.response.category.CategoryListResponseDto;
import com.ojeomme.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<CategoryListResponseDto> getCategoryList(@RequestParam(required = false) Long upCategory, @RequestParam int depth) {
        CategoryListResponseDto responseDto = categoryService.getCategoryList(upCategory, depth);
        return ResponseEntity.ok(responseDto);
    }
}
