package com.ojeomme.dto.response.region;

import lombok.Getter;

import java.util.List;

@Getter
public class RegionCodeOfCoordResponseDto {

    private final List<String> codes;
    private final String address;

    public RegionCodeOfCoordResponseDto(List<String> codes, String address) {
        this.codes = codes;
        this.address = address;
    }
}
