package com.ojeomme.domain.eattogetherpost.repository;

import com.ojeomme.dto.response.eattogether.EatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostResponseDto;
import com.ojeomme.dto.response.eattogether.RecentEatTogetherPostListResponseDto;

import java.util.Optional;

public interface EatTogetherPostCustomRepository {

    Optional<EatTogetherPostResponseDto> getEatTogetherPost(Long userId, Long postId);

    EatTogetherPostListResponseDto getEatTogetherPostList(String code, Long moreId);

    RecentEatTogetherPostListResponseDto getRecentEatTogetherPostList(String regionCode);
}
