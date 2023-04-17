package com.ojeomme.controller;

import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.SearchPlaceListResponseDto;
import com.ojeomme.dto.response.store.StoreReviewsResponseDto;
import com.ojeomme.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/searchPlaceList")
    public ResponseEntity<SearchPlaceListResponseDto> searchPlaceList(@Valid SearchPlaceListRequestDto requestDto) {
        SearchPlaceListResponseDto responseDto = storeService.searchPlaceList(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreReviewsResponseDto> getStoreReviews(@PathVariable Long storeId) {
        StoreReviewsResponseDto responseDto = storeService.getStoreReviews(storeId);
        return ResponseEntity.ok(responseDto);
    }
}
