package com.ojeomme.dto.response.eattogether;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class RecentEatTogetherPostListResponseDto {

    private final List<PostResponseDto> posts;

    public RecentEatTogetherPostListResponseDto(List<PostResponseDto> posts) {
        this.posts = posts;
    }

    @NoArgsConstructor
    @Getter
    public static class PostResponseDto {

        private Long postId;
        private String regionName;
        private String subject;
        private String createDatetime;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime ltCreateDatetime;

        @Builder
        public PostResponseDto(Long postId, String regionName, String subject, LocalDateTime ltCreateDatetime) {
            this.postId = postId;
            this.regionName = regionName;
            this.subject = subject;
            this.ltCreateDatetime = ltCreateDatetime;
        }

        public void convertDatetime() {
            LocalDate now = LocalDate.now();
            if (now.getYear() == ltCreateDatetime.getYear() &&
                    now.getMonthValue() == ltCreateDatetime.getMonthValue() &&
                    now.getDayOfMonth() == ltCreateDatetime.getDayOfMonth()) {
                this.createDatetime = ltCreateDatetime.format(DateTimeFormatter.ofPattern("HH:mm"));
            } else {
                this.createDatetime = ltCreateDatetime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }

            this.ltCreateDatetime = null;
        }
    }
}
