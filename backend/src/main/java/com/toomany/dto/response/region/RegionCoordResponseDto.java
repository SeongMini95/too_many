package com.toomany.dto.response.region;

import lombok.Getter;

@Getter
public class RegionCoordResponseDto {

    private String x;
    private String y;

    public RegionCoordResponseDto(String x, String y) {
        this.x = x;
        this.y = y;
    }
}
