package com.toomany.controller;

import com.toomany.dto.request.auth.ReissueTokenRequestDto;
import com.toomany.dto.response.auth.LoginCheckResponseDto;
import com.toomany.dto.response.auth.LoginTokenResponseDto;
import com.toomany.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/{provider}/login/uri")
    public ResponseEntity<String> getLoginUri(@PathVariable String provider, @RequestParam String redirectUri) {
        String loginUri = authService.getLoginUri(provider, redirectUri);
        return ResponseEntity.ok(loginUri);
    }

    @GetMapping("/{provider}/login")
    public ResponseEntity<LoginTokenResponseDto> login(@PathVariable String provider, @RequestParam String redirectUri, @RequestParam String code) {
        LoginTokenResponseDto responseDto = authService.login(provider, redirectUri, code);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginTokenResponseDto> reissue(HttpServletRequest request, @Valid @RequestBody ReissueTokenRequestDto requestDto) {
        LoginTokenResponseDto responseDto = authService.reissue(request, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/check")
    public ResponseEntity<LoginCheckResponseDto> check(HttpServletRequest request) {
        LoginCheckResponseDto responseDto = authService.check(request);
        return ResponseEntity.ok(responseDto);
    }
}
