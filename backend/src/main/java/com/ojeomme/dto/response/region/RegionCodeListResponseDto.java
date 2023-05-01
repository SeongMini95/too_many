package com.ojeomme.dto.response.region;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ojeomme.domain.regioncode.RegionCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RegionCodeListResponseDto implements Serializable {

    private final List<RegionCodeResponseDto> regions;

    public RegionCodeListResponseDto(List<RegionCode> regionCodes) {
        List<RegionCode> depth1List = regionCodes.stream().filter(v -> v.getRegionDepth() == 1).collect(Collectors.toList());
        List<RegionCode> depth2List = regionCodes.stream().filter(v -> v.getRegionDepth() == 2).collect(Collectors.toList());
        List<RegionCode> depth3List = regionCodes.stream().filter(v -> v.getRegionDepth() == 3).collect(Collectors.toList());

        this.regions = depth1List.stream()
                .map(v1 -> new RegionCodeResponseDto(v1, depth2List.stream()
                        .filter(v2 -> v1.getCode().equals(v2.getUpCode().getCode()))
                        .map(v2 -> new RegionCodeResponseDto(v2, depth3List.stream()
                                .filter(v3 -> v2.getCode().equals(v3.getUpCode().getCode()))
                                .map(RegionCodeResponseDto::new)
                                .sorted(Comparator.comparing(RegionCodeResponseDto::getName))
                                .collect(Collectors.toList())))
                        .sorted(Comparator.comparing(RegionCodeResponseDto::getName))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Getter
    public static class RegionCodeResponseDto {

        private final String code;
        private final String name;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private final List<RegionCodeResponseDto> children;

        public RegionCodeResponseDto(RegionCode regionCode) {
            this.code = regionCode.getCode();
            this.name = regionCode.getRegionName();
            this.children = Collections.emptyList();
        }

        public RegionCodeResponseDto(RegionCode regionCode, List<RegionCodeResponseDto> children) {
            this.code = regionCode.getCode();
            this.name = regionCode.getRegionName();
            this.children = children;
        }
    }
}
