package com.ojeomme.dto.response.eattogether;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EatTogetherReplyListResponseDto {

    private final List<ReplyResponseDto> replies;

    public EatTogetherReplyListResponseDto(List<ReplyResponseDto> replies) {
        this.replies = replies;
    }

    @NoArgsConstructor
    @Getter
    public static class ReplyResponseDto {

        private Long replyId;
        
        private Long userId;

        private String nickname;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String upNickname;

        private String content;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String image;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
        private LocalDateTime createDatetime;

        @Builder
        public ReplyResponseDto(Long replyId, Long userId, String nickname, String upNickname, String content, String image, LocalDateTime createDatetime) {
            this.replyId = replyId;
            this.userId = userId;
            this.nickname = nickname;
            this.upNickname = upNickname;
            this.content = content;
            this.image = image;
            this.createDatetime = createDatetime;
        }
    }
}
