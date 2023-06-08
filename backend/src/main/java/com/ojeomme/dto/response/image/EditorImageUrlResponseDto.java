package com.ojeomme.dto.response.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EditorImageUrlResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String url;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final UploadErrorResponseDto error;

    @Builder
    public EditorImageUrlResponseDto(String url, UploadErrorResponseDto error) {
        this.url = url;
        this.error = error;
    }

    public static EditorImageUrlResponseDto success(String url) {
        return EditorImageUrlResponseDto.builder()
                .url(url)
                .build();
    }

    public static EditorImageUrlResponseDto fail(String message) {
        return EditorImageUrlResponseDto.builder()
                .error(new UploadErrorResponseDto(message))
                .build();
    }

    @Getter
    public static class UploadErrorResponseDto {

        private final String message;

        public UploadErrorResponseDto(String message) {
            this.message = message;
        }
    }
}
