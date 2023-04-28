package com.ojeomme.domain.eattogetherreply.repository;

import com.ojeomme.dto.response.eattogether.EatTogetherReplyListResponseDto;

public interface EatTogetherReplyCustomRepository {

    EatTogetherReplyListResponseDto getReplyList(Long postId);
}
