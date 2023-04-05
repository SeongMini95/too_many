package com.toomany.domain.category.repository;

import com.toomany.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryDepthAndCategoryName(int categoryDepth, String categoryName);

    boolean existsByCategoryDepthAndCategoryName(int categoryDepth, String categoryName);
}