package com.ojeomme.dto.response.eattogether;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class EatTogetherPostListResponseDto {

    private final List<PostResponseDto> posts;

    public EatTogetherPostListResponseDto(List<PostResponseDto> posts) {
        posts.forEach(v -> v.convertCreateDatetime(LocalDateTime.now().getDayOfMonth()));
        this.posts = posts;
    }

    @NoArgsConstructor
    @Getter
    public static class PostResponseDto {

        private Long postId;
        private String nickname;
        private String regionName;
        private String subject;
        private long replyCnt;
        private String createDatetime;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime oriCreateDatetime;

        @Builder
        public PostResponseDto(Long postId, String nickname, String regionName, String subject, long replyCnt, String createDatetime, LocalDateTime oriCreateDatetime) {
            this.postId = postId;
            this.nickname = nickname;
            this.regionName = regionName;
            this.subject = subject;
            this.replyCnt = replyCnt;
            this.createDatetime = createDatetime;
            this.oriCreateDatetime = oriCreateDatetime;
        }

        public void convertCreateDatetime(int day) {
            if (day == oriCreateDatetime.getDayOfMonth()) {
                createDatetime = oriCreateDatetime.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                createDatetime = oriCreateDatetime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd."));
            }

            oriCreateDatetime = null;
        }
    }
}
