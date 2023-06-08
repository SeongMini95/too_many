package com.ojeomme.dto.response.reviewimage;

import lombok.Getter;

import java.util.List;

@Getter
public class PreviewImageListResponseDto {

    private final long imageCnt;
    private final List<String> images;

    public PreviewImageListResponseDto(long imageCnt, List<String> images) {
        this.imageCnt = imageCnt;
        this.images = images;
    }
}
