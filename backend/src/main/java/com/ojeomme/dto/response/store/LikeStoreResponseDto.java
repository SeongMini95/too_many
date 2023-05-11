package com.ojeomme.dto.response.store;

import lombok.Getter;

@Getter
public class LikeStoreResponseDto {

    private final boolean result;
    private final int likeCnt;

    public LikeStoreResponseDto(boolean result, int likeCnt) {
        this.result = result;
        this.likeCnt = likeCnt;
    }
}
