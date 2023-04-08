package com.ojeomme.controller;

import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.SearchStoreListResponseDto;
import com.ojeomme.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/searchStoreList")
    public ResponseEntity<SearchStoreListResponseDto> searchPlaceList(SearchPlaceListRequestDto requestDto) {
        SearchStoreListResponseDto responseDto = storeService.searchStoreList(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
