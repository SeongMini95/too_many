package com.ojeomme.controller;

import com.ojeomme.dto.response.region.CoordOfRegionResponseDto;
import com.ojeomme.dto.response.region.RegionCodeListResponseDto;
import com.ojeomme.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/region")
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/regionOfCoord")
    public ResponseEntity<String> getRegionCodeOfCoord(@RequestParam String x, @RequestParam String y) {
        String code = regionService.getRegionCodeOfCoord(x, y);
        return ResponseEntity.ok(code);
    }

    @GetMapping("/coordOfRegion")
    public ResponseEntity<CoordOfRegionResponseDto> getCoordOfRegionCode(@RequestParam String code) {
        CoordOfRegionResponseDto responseDto = regionService.getCoordOfRegionCode(code);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/list")
    public ResponseEntity<RegionCodeListResponseDto> getRegionCodeList() {
        RegionCodeListResponseDto responseDto = regionService.getRegionCodeList();
        return ResponseEntity.ok(responseDto);
    }
}
