package com.ojeomme.domain.category.repository;

import java.util.Set;

public interface CategoryCustomRepository {

    Set<Long> getDownCategory(Long categoryId);
}
