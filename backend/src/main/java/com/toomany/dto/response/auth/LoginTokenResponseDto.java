package com.toomany.dto.response.auth;

import lombok.Getter;

@Getter
public class LoginTokenResponseDto {

    private final String accessToken;
    private final String refreshToken;

    public LoginTokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
