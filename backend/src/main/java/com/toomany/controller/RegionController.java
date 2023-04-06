package com.toomany.controller;

import com.toomany.service.RegionService;
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

    @GetMapping("/coord")
    public ResponseEntity<String> getRegionCodeOfCoord(@RequestParam String x, @RequestParam String y) {
        String code = regionService.getRegionCodeOfCoord(x, y);
        return ResponseEntity.ok(code);
    }
}
