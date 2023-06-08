package com.ojeomme.dto.response.region;

import lombok.Getter;

@Getter
public class CoordOfRegionResponseDto {

    private final String address;
    private final String x;
    private final String y;

    public CoordOfRegionResponseDto(String address, String x, String y) {
        this.address = address;
        this.x = x;
        this.y = y;
    }
}
