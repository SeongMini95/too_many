package com.ojeomme.service;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherpost.repository.EatTogetherPostRepository;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherPostRequestDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class EatTogetherService {

    private final UserRepository userRepository;
    private final RegionCodeRepository regionCodeRepository;
    private final EatTogetherPostRepository eatTogetherPostRepository;
    private final ImageService imageService;

    @Value("${image.host}")
    private String host;

    @Transactional
    public Long writeEatTogetherPost(Long userId, WriteEatTogetherPostRequestDto requestDto) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        RegionCode regionCode = regionCodeRepository.findById(requestDto.getRegionCode()).orElseThrow(() -> new ApiException(ApiErrorCode.REGION_CODE_NOT_FOUND));

        // 임시 이미지 추출 후 변환
        List<String> images = new ArrayList<>();
        String convertContent = requestDto.getContent();
        Pattern p = Pattern.compile("<img.*src=\"" + host + "([^\"]*).*\">");
        Matcher m = p.matcher(requestDto.getContent());

        while (m.find()) {
            String tempUrl = UriComponentsBuilder.fromHttpUrl(host).path(m.group(1)).toUriString();
            String url = imageService.copyImage(tempUrl);

            convertContent = convertContent.replaceFirst(tempUrl, url);
            images.add(url);
        }

        // 게시글 저장
        EatTogetherPost eatTogetherPost = eatTogetherPostRepository.save(requestDto.toEntity(user, regionCode, convertContent));
        eatTogetherPost.addImages(requestDto.toTogetherBoardImages(eatTogetherPost, images));

        return eatTogetherPost.getId();
    }
}
