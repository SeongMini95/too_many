package com.ojeomme.domain.eattogetherpost;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.eattogetherpostimage.EatTogetherPostImage;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "eat_together_post")
public class EatTogetherPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_code", nullable = false)
    private RegionCode regionCode;

    @Column(name = "subject", nullable = false, length = 30)
    private String subject;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @OneToMany(mappedBy = "eatTogetherPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EatTogetherPostImage> images = new LinkedHashSet<>();

    @Builder
    public EatTogetherPost(User user, RegionCode regionCode, String subject, String content) {
        this.user = user;
        this.regionCode = regionCode;
        this.subject = subject;
        this.content = content;
    }

    public void addImages(Set<EatTogetherPostImage> images) {
        this.images.addAll(images);
    }
}