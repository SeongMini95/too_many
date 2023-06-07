package com.ojeomme.controller;

import com.ojeomme.config.auth.LoginUser;
import com.ojeomme.dto.request.user.ModifyMyInfoRequestDto;
import com.ojeomme.dto.response.user.ModifyMyInfoResponseDto;
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

    @PutMapping("/my")
    public ResponseEntity<ModifyMyInfoResponseDto> modifyMyInfo(@LoginUser Long userId, @Valid @RequestBody ModifyMyInfoRequestDto requestDto) throws IOException {
        ModifyMyInfoResponseDto responseDto = userService.modifyMyInfo(userId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
