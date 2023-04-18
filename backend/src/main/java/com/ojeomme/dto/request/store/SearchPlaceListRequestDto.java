package com.ojeomme.dto.request.store;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class SearchPlaceListRequestDto {

    @NotNull(message = "검색어를 입력하세요.")
    @NotBlank(message = "검색어를 입력하세요.")
    private String query;

    @NotNull(message = "지역을 선택하세요.")
    @NotBlank(message = "지역을 선택하세요.")
    private String x;

    @NotNull(message = "지역을 선택하세요.")
    @NotBlank(message = "지역을 선택하세요.")
    private String y;
    private Integer page;

    public void setPage(Integer page) {
        this.page = page != null && page > 0 ? page : 1;
    }

    @Builder
    public SearchPlaceListRequestDto(String query, String x, String y, Integer page) {
        this.query = query;
        this.x = x;
        this.y = y;
        this.page = page;
    }
}
