package com.ojeomme.dto.request.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
public class ModifyNicknameRequestDto {

    @NotNull(message = "닉네임을 입력하세요.")
    @NotBlank(message = "닉네임을 입력하세요.")
    @Size(min = 2, max = 15, message = "닉네임은 2 ~ 15자 입니다.")
    private String nickname;

    @Builder
    public ModifyNicknameRequestDto(String nickname) {
        this.nickname = nickname;
    }
}
