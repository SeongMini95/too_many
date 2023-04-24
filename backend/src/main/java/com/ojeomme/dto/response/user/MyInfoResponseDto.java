package com.ojeomme.dto.response.user;

import com.ojeomme.domain.user.User;
import lombok.Getter;

@Getter
public class MyInfoResponseDto {

    private final String provider;
    private final String nickname;
    private final String email;
    private final String profile;

    public MyInfoResponseDto(User user) {
        this.provider = user.getOauthProvider().getCode();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.profile = user.getProfile();
    }
}
