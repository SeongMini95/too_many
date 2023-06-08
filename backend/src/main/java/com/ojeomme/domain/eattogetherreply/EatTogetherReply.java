package com.ojeomme.domain.eattogetherreply;

import com.ojeomme.domain.BaseTimeEntity;
import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "eat_together_reply")
public class EatTogetherReply extends BaseTimeEntity implements Persistable<Long> {

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

    @Column(name = "image_url", nullable = false, length = 2083)
    private String imageUrl;

    @Column(name = "delete_yn", nullable = false)
    private boolean deleteYn;

    @Builder
    public EatTogetherReply(Long id, User user, EatTogetherPost eatTogetherPost, Long upId, String content, String imageUrl, boolean deleteYn) {
        this.id = id;
        this.user = user;
        this.eatTogetherPost = eatTogetherPost;
        this.upId = upId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.deleteYn = deleteYn;
    }

    @Override
    public boolean isNew() {
        return getCreateDatetime() == null;
    }

    public void modifyContent(String content) {
        this.content = content;
    }

    public void modifyImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void delete() {
        this.deleteYn = true;
    }
}