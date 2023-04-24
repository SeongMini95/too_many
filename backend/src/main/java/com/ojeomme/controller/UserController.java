package com.ojeomme.controller;

import com.ojeomme.config.auth.LoginUser;
import com.ojeomme.dto.request.user.ModifyNicknameRequestDto;
import com.ojeomme.dto.request.user.ModifyProfileRequestDto;
import com.ojeomme.dto.response.user.MyInfoResponseDto;
import com.ojeomme.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    public ResponseEntity<MyInfoResponseDto> getMyInfo(@LoginUser Long userId) {
        MyInfoResponseDto responseDto = userService.getMyInfo(userId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/my/nickname")
    public ResponseEntity<Void> modifyNickname(@LoginUser Long userId, @Valid @RequestBody ModifyNicknameRequestDto requestDto) {
        userService.modifyNickname(userId, requestDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/my/profile")
    public ResponseEntity<String> modifyProfile(@LoginUser Long userId, @Valid @RequestBody ModifyProfileRequestDto requestDto) throws IOException {
        String profile = userService.modifyProfile(userId, requestDto);
        return ResponseEntity.ok(profile);
    }
}
