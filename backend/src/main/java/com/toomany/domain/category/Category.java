package com.toomany.domain.category;

import com.toomany.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "categories")
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_category_id")
    private Category upCategory;

    @Column(name = "category_depth", nullable = false)
    private int categoryDepth;

    @Column(name = "category_name", nullable = false, length = 20)
    private String categoryName;

    @OneToMany(mappedBy = "upCategory")
    private List<Category> categories = new ArrayList<>();

    @Builder
    public Category(Category upCategory, int categoryDepth, String categoryName) {
        this.upCategory = upCategory;
        this.categoryDepth = categoryDepth;
        this.categoryName = categoryName;
    }
}