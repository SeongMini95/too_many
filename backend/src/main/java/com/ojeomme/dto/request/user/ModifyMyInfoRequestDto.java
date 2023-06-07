package com.ojeomme.dto.request.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ModifyMyInfoRequestDto {

    private String nickname;
    private String profile;

    @Builder
    public ModifyMyInfoRequestDto(String nickname, String profile) {
        this.nickname = nickname;
        this.profile = profile;
    }
}
