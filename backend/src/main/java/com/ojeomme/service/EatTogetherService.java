package com.ojeomme.service;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherpost.repository.EatTogetherPostRepository;
import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import com.ojeomme.domain.eattogetherreply.repository.EatTogetherReplyRepository;
import com.ojeomme.domain.eattogetherreplyimage.repository.EatTogetherReplyImageRepository;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherPostRequestDto;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherReplyRequestDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private final EatTogetherReplyRepository eatTogetherReplyRepository;
    private final EatTogetherReplyImageRepository eatTogetherReplyImageRepository;

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

    @Transactional(readOnly = true)
    public EatTogetherPostResponseDto getEatTogetherPost(Long postId) {
        return eatTogetherPostRepository.getEatTogetherPost(postId).orElseThrow(() -> new ApiException(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public EatTogetherPostListResponseDto getEatTogetherPostList(String regionCode, Long moreId) {
        return eatTogetherPostRepository.getEatTogetherPostList(regionCode, moreId);
    }

    @Transactional
    public void writeEatTogetherReply(Long userId, Long postId, WriteEatTogetherReplyRequestDto requestDto) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND));
        EatTogetherPost eatTogetherPost = eatTogetherPostRepository.findById(postId).orElseThrow(() -> new ApiException(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND));

        Long upReplyId = requestDto.getUpReplyId();
        if (upReplyId != null && eatTogetherReplyRepository.findById(upReplyId).isEmpty()) {
            throw new ApiException(ApiErrorCode.EAT_TOGETHER_REPLY_NOT_FOUND);
        }

        Long seq = eatTogetherReplyRepository.nextval();
        EatTogetherReply eatTogetherReply = eatTogetherReplyRepository.save(requestDto.toEntity(seq, user, eatTogetherPost));

        String image = requestDto.getImage();
        if (StringUtils.isNotBlank(image)) {
            image = imageService.copyImage(image);
            eatTogetherReplyImageRepository.save(requestDto.toEntity(eatTogetherReply, image));
        }
    }
}
