package com.ojeomme.domain.eattogetherreplyimage;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "eat_together_reply_image")
public class EatTogetherReplyImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reply_id", nullable = false)
    private EatTogetherReply eatTogetherReply;

    @Column(name = "image_url", nullable = false, length = 2083)
    private String imageUrl;

    @Builder
    public EatTogetherReplyImage(EatTogetherReply eatTogetherReply, String imageUrl) {
        this.eatTogetherReply = eatTogetherReply;
        this.imageUrl = imageUrl;
    }
}