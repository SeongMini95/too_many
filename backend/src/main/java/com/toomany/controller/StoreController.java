package com.toomany.controller;

import com.toomany.dto.request.store.SearchStoreListRequestDto;
import com.toomany.dto.response.store.SearchStoreListResponseDto;
import com.toomany.service.StoreService;
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
    public ResponseEntity<SearchStoreListResponseDto> searchStoreList(SearchStoreListRequestDto requestDto) {
        SearchStoreListResponseDto responseDto = storeService.searchStoreList(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
