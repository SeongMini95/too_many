package com.toomany.dto.request.store;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@Getter
@Setter
public class SearchStoreRequestDto {

    private String query;
    private String x;
    private String y;
    private Integer page;

    public void setX(String x) {
        this.x = !StringUtils.isBlank(x) ? x : "";
    }

    public void setY(String y) {
        this.y = !StringUtils.isBlank(y) ? y : "";
    }

    public void setPage(Integer page) {
        this.page = page != null && page > 0 ? page : 1;
    }

    @Builder
    public SearchStoreRequestDto(String query, String x, String y, Integer page) {
        this.query = query;
        this.x = x;
        this.y = y;
        this.page = page;
    }
}
