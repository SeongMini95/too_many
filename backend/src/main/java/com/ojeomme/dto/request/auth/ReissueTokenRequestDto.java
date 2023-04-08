package com.ojeomme.dto.request.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReissueTokenRequestDto {

    private String refreshToken;

    @Builder
    public ReissueTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
