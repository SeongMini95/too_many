package com.ojeomme.dto.response.eattogether;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class EatTogetherPostResponseDto {

    private long postId;
    private boolean isWrite;
    private String nickname;
    private String profile;
    private String regionCode;
    private String regionName;
    private String subject;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd. HH:mm:ss")
    private LocalDateTime createDatetime;

    @Builder
    public EatTogetherPostResponseDto(long postId, boolean isWrite, String nickname, String profile, String regionCode, String regionName, String subject, String content, LocalDateTime createDatetime) {
        this.postId = postId;
        this.isWrite = isWrite;
        this.nickname = nickname;
        this.profile = profile;
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.subject = subject;
        this.content = content;
        this.createDatetime = createDatetime;
    }

    public boolean getIsWrite() {
        return isWrite;
    }
}
