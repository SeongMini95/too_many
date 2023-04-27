package com.ojeomme.controller;

import com.ojeomme.config.auth.LoginUser;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherPostRequestDto;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherReplyRequestDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostResponseDto;
import com.ojeomme.service.EatTogetherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/eatTogether")
public class EatTogetherController {

    private final EatTogetherService eatTogetherService;

    @PostMapping("/post")
    public ResponseEntity<Long> writeEatTogetherPost(@LoginUser Long userId, @Valid @RequestBody WriteEatTogetherPostRequestDto requestDto) throws IOException {
        Long postId = eatTogetherService.writeEatTogetherPost(userId, requestDto);
        return ResponseEntity.ok(postId);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<EatTogetherPostResponseDto> getEatTogetherPost(@PathVariable Long postId) {
        EatTogetherPostResponseDto responseDto = eatTogetherService.getEatTogetherPost(postId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/post/list")
    public ResponseEntity<EatTogetherPostListResponseDto> getEatTogetherPostList(@RequestParam String regionCode, @RequestParam(required = false) Long moreId) {
        EatTogetherPostListResponseDto responseDto = eatTogetherService.getEatTogetherPostList(regionCode, moreId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/post/{postId}/reply")
    public ResponseEntity<Void> writeEatTogetherReply(@LoginUser Long userId, @PathVariable Long postId, @Valid @RequestBody WriteEatTogetherReplyRequestDto requestDto) throws IOException {
        eatTogetherService.writeEatTogetherReply(userId, postId, requestDto);
        return ResponseEntity.ok().build();
    }
}
