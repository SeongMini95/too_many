package com.ojeomme.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojeomme.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginCheckResponseDto {

    private final boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String nickname;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String profile;

    @Builder
    public LoginCheckResponseDto(boolean result, String nickname, String profile) {
        this.result = result;
        this.nickname = nickname;
        this.profile = profile;
    }

    public static LoginCheckResponseDto success(User user) {
        return LoginCheckResponseDto.builder()
                .result(true)
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .build();
    }

    public static LoginCheckResponseDto fail() {
        return LoginCheckResponseDto.builder()
                .result(false)
                .build();
    }
}
