package com.ojeomme.domain.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.ojeomme.domain.category.QCategory.category;

@RequiredArgsConstructor
public class CategoryCustomRepositoryImpl implements CategoryCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Set<Long> getDownCategory(Long categoryId) {
        int depth = factory
                .select(category.categoryDepth)
                .from(category)
                .where(category.id.eq(categoryId))
                .fetchFirst();

        Set<Long> categories = new HashSet<>();
        categories.add(categoryId);

        for (int i = depth + 1; i <= 3; i++) {
            categories.addAll(factory
                    .select(category.id)
                    .from(category)
                    .where(
                            category.upCategory.id.in(categories),
                            category.categoryDepth.eq(i)
                    )
                    .fetch());
        }

        return categories;
    }
}
