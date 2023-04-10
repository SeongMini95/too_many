package com.ojeomme.service;

import com.ojeomme.common.maps.client.KakaoAddressClient;
import com.ojeomme.common.maps.client.KakaoRegionCodeClient;
import com.ojeomme.common.maps.entity.KakaoAddressCoord;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.response.region.CoordOfRegionResponseDto;
import com.ojeomme.dto.response.region.RegionCodeListResponseDto;
import com.ojeomme.dto.response.region.RegionCodeOfCoordResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RegionService {

    private final RegionCodeRepository regionCodeRepository;
    private final KakaoRegionCodeClient kakaoRegionCodeClient;
    private final KakaoAddressClient kakaoAddressClient;

    @Transactional(readOnly = true)
    public RegionCodeOfCoordResponseDto getRegionCodeOfCoord(String x, String y) {
        String kakaoCode = kakaoRegionCodeClient.getRegionCode(x, y).getCode();

        RegionCode regionCode = regionCodeRepository.findById(kakaoCode).orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));
        if (regionCode.getRegionDepth() == 4) {
            regionCode = regionCode.getUpCode();
        }

        // 마지막 지역 코드로 주소를 얻는다.
        String address = getAddressOfRegionCode(regionCode);

        return new RegionCodeOfCoordResponseDto(regionCode.getCode(), address);
    }

    @Transactional(readOnly = true)
    public CoordOfRegionResponseDto getCoordOfRegionCode(String code) {
        RegionCode regionCode = regionCodeRepository.findById(code).orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));
        if (regionCode.getRegionDepth() == 4) {
            regionCode = regionCode.getUpCode();
        }

        // 마지막 지역 코드로 주소를 얻는다.
        String address = getAddressOfRegionCode(regionCode);

        // 주소로 좌표를 얻는다.
        KakaoAddressCoord kakaoAddressCoord = kakaoAddressClient.getKakaoAddressCoord(address);

        return new CoordOfRegionResponseDto(address, kakaoAddressCoord.getX(), kakaoAddressCoord.getY());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "regionCodeList", key = "#root.methodName")
    public RegionCodeListResponseDto getRegionCodeList() {
        List<RegionCode> regionCodes = regionCodeRepository.findAllByRegionDepthNot(4);
        return new RegionCodeListResponseDto(regionCodes);
    }

    private String getAddressOfRegionCode(RegionCode regionCode) {
        List<RegionCode> regionCodes = new ArrayList<>();
        regionCodes.add(regionCode);

        int regionDepth = regionCode.getRegionDepth();
        for (int i = 1; i < regionDepth; i++) {
            RegionCode upRegionCode = regionCodeRepository.findById(regionCodes.get(0).getUpCode().getCode())
                    .orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));
            regionCodes.add(0, upRegionCode);
        }

        return regionCodes.stream()
                .map(RegionCode::getRegionName)
                .collect(Collectors.joining(" "));
    }
}
