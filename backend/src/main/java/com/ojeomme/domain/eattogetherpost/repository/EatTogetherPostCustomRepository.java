package com.ojeomme.domain.eattogetherpost.repository;

import com.ojeomme.dto.response.eattogether.EatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostResponseDto;

import java.util.Optional;

public interface EatTogetherPostCustomRepository {

    Optional<EatTogetherPostResponseDto> getEatTogetherPost(Long postId);

    EatTogetherPostListResponseDto getEatTogetherPostList(String regionCode, Long moreId);
}
