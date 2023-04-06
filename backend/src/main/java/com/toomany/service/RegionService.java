package com.toomany.service;

import com.toomany.common.maps.client.KakaoAddressClient;
import com.toomany.common.maps.client.KakaoRegionCodeClient;
import com.toomany.common.maps.entity.KakaoAddressCoord;
import com.toomany.domain.regioncode.RegionCode;
import com.toomany.domain.regioncode.repository.RegionCodeRepository;
import com.toomany.dto.response.region.RegionCoordResponseDto;
import com.toomany.exception.ApiErrorCode;
import com.toomany.exception.ApiException;
import lombok.RequiredArgsConstructor;
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
    public String getRegionCodeOfCoord(String x, String y) {
        String kakaoCode = kakaoRegionCodeClient.getRegionCode(x, y).getCode();

        RegionCode regionCode = regionCodeRepository.findById(kakaoCode).orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));
        if (regionCode.getRegionDepth() == 4) { // depth가 4면 상위 지역 가져온다.
            return regionCode.getUpCode().getCode();
        }

        return regionCode.getCode();
    }

    @Transactional(readOnly = true)
    public RegionCoordResponseDto getRegionCoord(String code) {
        RegionCode regionCode = regionCodeRepository.findById(code).orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));

        // 마지막 지역 코드로 주소를 얻는다.
        List<RegionCode> regionCodes = new ArrayList<>();
        regionCodes.add(regionCode);

        int regionDepth = regionCode.getRegionDepth();
        for (int i = 1; i < regionDepth; i++) {
            RegionCode upRegionCode = regionCodeRepository.findById(regionCodes.get(0).getUpCode().getCode())
                    .orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));
            regionCodes.add(0, upRegionCode);
        }

        // 주소로 좌표를 얻는다.
        String address = regionCodes.stream()
                .map(RegionCode::getRegionName)
                .collect(Collectors.joining(" "));
        KakaoAddressCoord kakaoAddressCoord = kakaoAddressClient.getKakaoAddressCoord(address);

        return new RegionCoordResponseDto(kakaoAddressCoord.getX(), kakaoAddressCoord.getY());
    }
}
