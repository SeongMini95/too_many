package com.ojeomme.service;

import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.user.ModifyNicknameRequestDto;
import com.ojeomme.dto.request.user.ModifyProfileRequestDto;
import com.ojeomme.dto.response.user.MyInfoResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public MyInfoResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        return new MyInfoResponseDto(user);
    }

    @Transactional
    public void modifyNickname(Long userId, ModifyNicknameRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        user.modifyNickname(requestDto.getNickname());
    }

    @Transactional
    public String modifyProfile(Long userId, ModifyProfileRequestDto requestDto) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        String profile = requestDto.getProfile();
        if (StringUtils.isBlank(profile)) { // 기본 프로필
            user.setDefaultProfile();

            return "";
        } else {
            profile = imageService.copyImage(profile);
            user.modifyProfile(profile);

            return profile;
        }
    }
}
