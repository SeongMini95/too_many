package com.ojeomme.dto.response.eattogether;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class EatTogetherPostResponseDto {

    private Long id;
    private Long userId;
    private String nickname;
    private String regionCode;
    private String regionName;
    private String subject;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH:mm:ss")
    private LocalDateTime createDatetime;

    @Builder
    public EatTogetherPostResponseDto(Long id, Long userId, String nickname, String regionCode, String regionName, String subject, String content, LocalDateTime createDatetime) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.subject = subject;
        this.content = content;
        this.createDatetime = createDatetime;
    }
}
