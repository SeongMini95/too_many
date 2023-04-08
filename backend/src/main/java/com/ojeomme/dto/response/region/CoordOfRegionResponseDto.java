package com.ojeomme.dto.response.region;

import lombok.Getter;

@Getter
public class CoordOfRegionResponseDto {

    private final String x;
    private final String y;

    public CoordOfRegionResponseDto(String x, String y) {
        this.x = x;
        this.y = y;
    }
}
