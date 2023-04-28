package com.ojeomme.domain.eattogetherreply;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "eat_together_reply")
public class EatTogetherReply extends BaseTimeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private EatTogetherPost eatTogetherPost;

    @Column(name = "up_id", nullable = false)
    private Long upId;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Builder
    public EatTogetherReply(Long id, User user, EatTogetherPost eatTogetherPost, Long upId, String content) {
        this.id = id;
        this.user = user;
        this.eatTogetherPost = eatTogetherPost;
        this.upId = upId;
        this.content = content;
    }
}