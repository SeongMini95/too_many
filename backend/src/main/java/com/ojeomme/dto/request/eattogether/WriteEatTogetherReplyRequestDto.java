package com.ojeomme.dto.request.eattogether;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import com.ojeomme.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
public class WriteEatTogetherReplyRequestDto {

    private Long upReplyId;

    @NotNull(message = "댓글을 입력하세요.")
    @NotBlank(message = "댓글을 입력하세요.")
    @Size(max = 3000, message = "댓글은 최대 3000자 입니다.")
    private String content;

    @URL(message = "이미지 URL 형식이 잘못되었습니다.")
    private String image;

    @Builder
    public WriteEatTogetherReplyRequestDto(Long upReplyId, String content, String image) {
        this.upReplyId = upReplyId;
        this.content = content;
        this.image = image;
    }

    public EatTogetherReply toEntity(Long seq, User user, EatTogetherPost eatTogetherPost, String imageUrl) {
        return EatTogetherReply.builder()
                .id(seq)
                .user(user)
                .eatTogetherPost(eatTogetherPost)
                .upId(upReplyId == null ? seq : upReplyId)
                .content(content.trim())
                .imageUrl(imageUrl)
                .deleteYn(false)
                .build();
    }
}
