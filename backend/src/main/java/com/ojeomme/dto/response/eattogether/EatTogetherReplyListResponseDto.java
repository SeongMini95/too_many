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

    private final Long replyCnt;
    private final List<ReplyResponseDto> replies;

    public EatTogetherReplyListResponseDto(Long replyCnt, List<ReplyResponseDto> replies) {
        this.replyCnt = replyCnt;
        this.replies = replies;
    }

    @NoArgsConstructor
    @Getter
    public static class ReplyResponseDto {

        private long replyId;

        private boolean isWrite;

        private boolean isWriter;

        private String nickname;

        private String profile;

        private long upReplyId;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String upNickname;

        private String content;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String image;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd. HH:mm:ss")
        private LocalDateTime createDatetime;

        @Builder
        public ReplyResponseDto(long replyId, boolean isWrite, boolean isWriter, String nickname, String profile, long upReplyId, String upNickname, String content, String image, LocalDateTime createDatetime) {
            this.replyId = replyId;
            this.isWrite = isWrite;
            this.isWriter = isWriter;
            this.nickname = nickname;
            this.profile = profile;
            this.upReplyId = upReplyId;
            this.upNickname = upNickname;
            this.content = content;
            this.image = image;
            this.createDatetime = createDatetime;
        }

        public boolean getIsWrite() {
            return isWrite;
        }

        public boolean getIsWriter() {
            return isWriter;
        }
    }
}
