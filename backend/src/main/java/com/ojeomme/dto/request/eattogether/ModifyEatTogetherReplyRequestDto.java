package com.ojeomme.dto.request.eattogether;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
public class ModifyEatTogetherReplyRequestDto {

    @NotNull(message = "댓글을 입력하세요.")
    @NotBlank(message = "댓글을 입력하세요.")
    @Size(max = 3000, message = "댓글은 최대 3000자 입니다.")
    private String content;

    @URL(message = "이미지 URL 형식이 잘못되었습니다.")
    private String image;

    @Builder
    public ModifyEatTogetherReplyRequestDto(String content, String image) {
        this.content = content;
        this.image = image;
    }
}
