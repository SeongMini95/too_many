package com.ojeomme.controller;

import com.ojeomme.config.auth.LoginUser;
import com.ojeomme.dto.request.store.SearchPlaceListRequestDto;
import com.ojeomme.dto.response.store.SearchPlaceListResponseDto;
import com.ojeomme.dto.response.store.StorePreviewImagesResponseDto;
import com.ojeomme.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<StorePreviewImagesResponseDto> getStoreReviews(@PathVariable Long storeId) {
        StorePreviewImagesResponseDto responseDto = storeService.getStore(storeId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{storeId}/like")
    public ResponseEntity<Boolean> likeStore(@LoginUser Long userId, @PathVariable Long storeId) {
        boolean savedYn = storeService.likeStore(userId, storeId);
        return ResponseEntity.ok(savedYn);
    }

    @GetMapping("/{storeId}/like")
    public ResponseEntity<Boolean> getStoreLikeLogOfUser(@LoginUser Long userId, @PathVariable Long storeId) {
        boolean exist = storeService.getStoreLikeLogOfUser(userId, storeId);
        return ResponseEntity.ok(exist);
    }
}
