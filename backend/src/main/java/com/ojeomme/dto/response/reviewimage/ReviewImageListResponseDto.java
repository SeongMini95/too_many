package com.ojeomme.dto.response.reviewimage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
public class ReviewImageListResponseDto {

    private final boolean isEnd;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long moreId;

    private final List<String> images;

    public ReviewImageListResponseDto(boolean isEnd, Long moreId, List<String> images) {
        this.isEnd = isEnd;
        this.moreId = moreId;
        this.images = images;
    }

    public boolean getIsEnd() {
        return isEnd;
    }
}
