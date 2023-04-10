package com.ojeomme.dto.response.region;

import lombok.Getter;

@Getter
public class RegionCodeOfCoordResponseDto {

    private final String code;
    private final String address;

    public RegionCodeOfCoordResponseDto(String code, String address) {
        this.code = code;
        this.address = address;
    }
}
