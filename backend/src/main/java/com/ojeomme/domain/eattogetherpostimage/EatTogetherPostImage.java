package com.ojeomme.domain.eattogetherpostimage;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    public EatTogetherPostImage(EatTogetherPost eatTogetherPost, String imageUrl) {
        this.eatTogetherPost = eatTogetherPost;
        this.imageUrl = imageUrl;
    }
}