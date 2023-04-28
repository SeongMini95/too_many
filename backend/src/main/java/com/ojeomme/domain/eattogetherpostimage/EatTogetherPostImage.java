package com.ojeomme.domain.eattogetherpostimage;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "eat_together_post_image")
public class EatTogetherPostImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private EatTogetherPost eatTogetherPost;

    @Column(name = "image_url", nullable = false, length = 2083)
    private String imageUrl;

    @Builder
    public EatTogetherPostImage(Long id, EatTogetherPost eatTogetherPost, String imageUrl) {
        this.id = id;
        this.eatTogetherPost = eatTogetherPost;
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EatTogetherPostImage that = (EatTogetherPostImage) o;

        if (getId() != null && Objects.equals(getId(), that.getId())) {
            return true;
        }

        return Objects.equals(getEatTogetherPost().getId(), that.getEatTogetherPost().getId()) &&
                Objects.equals(getImageUrl(), that.getImageUrl());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}