package com.ojeomme.dto.response.user;

import lombok.Getter;

@Getter
public class ModifyMyInfoResponseDto {

    private final String nickname;
    private final String profile;

    public ModifyMyInfoResponseDto(String nickname, String profile) {
        this.nickname = nickname;
        this.profile = profile;
    }
}
