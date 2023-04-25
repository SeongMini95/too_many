package com.ojeomme.dto.request.eattogether;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherpostimage.EatTogetherPostImage;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class WriteEatTogetherPostRequestDto {

    @NotNull(message = "지역을 선택하세요.")
    @NotBlank(message = "지역을 선택하세요.")
    private String regionCode;

    @NotNull(message = "제목을 입력하세요.")
    @NotBlank(message = "제목을 입력하세요.")
    @Size(max = 30, message = "제목은 최대 30자 입니다.")
    private String subject;

    @NotNull(message = "내용을 입력하세요.")
    @NotBlank(message = "내용을 입력하세요.")
    private String content;

    @Builder
    public WriteEatTogetherPostRequestDto(String regionCode, String subject, String content) {
        this.regionCode = regionCode;
        this.subject = subject;
        this.content = content;
    }

    public EatTogetherPost toEntity(User user, RegionCode regionCode, String content) {
        return EatTogetherPost.builder()
                .user(user)
                .regionCode(regionCode)
                .subject(subject)
                .content(content)
                .build();
    }

    public Set<EatTogetherPostImage> toTogetherBoardImages(EatTogetherPost eatTogetherPost, List<String> images) {
        return images.stream()
                .map(v -> EatTogetherPostImage.builder()
                        .eatTogetherPost(eatTogetherPost)
                        .imageUrl(v)
                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
