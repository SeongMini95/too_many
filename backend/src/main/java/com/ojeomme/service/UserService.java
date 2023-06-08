package com.ojeomme.service;

import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.user.ModifyMyInfoRequestDto;
import com.ojeomme.dto.response.user.ModifyMyInfoResponseDto;
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
    public ModifyMyInfoResponseDto modifyMyInfo(Long userId, ModifyMyInfoRequestDto requestDto) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));

        user.modifyNickname(requestDto.getNickname());

        String profile = requestDto.getProfile();
        if (StringUtils.isBlank(profile)) {
            user.setDefaultProfile();
        } else {
            user.modifyProfile(imageService.copyImage(profile));
        }

        return new ModifyMyInfoResponseDto(user.getNickname(), user.getProfile());
    }
}
