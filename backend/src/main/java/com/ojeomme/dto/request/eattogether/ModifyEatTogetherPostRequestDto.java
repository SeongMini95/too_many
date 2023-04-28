package com.ojeomme.dto.request.eattogether;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherpostimage.EatTogetherPostImage;
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
public class ModifyEatTogetherPostRequestDto {

    @NotNull(message = "제목을 입력하세요.")
    @NotBlank(message = "제목을 입력하세요.")
    @Size(max = 30, message = "제목은 최대 30자 입니다.")
    private String subject;

    @NotNull(message = "내용을 입력하세요.")
    @NotBlank(message = "내용을 입력하세요.")
    private String content;

    @Builder
    public ModifyEatTogetherPostRequestDto(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public EatTogetherPost toEntity(EatTogetherPost eatTogetherPost, String content, List<String> images) {
        EatTogetherPost modifyEatTogetherPost = EatTogetherPost.builder()
                .subject(subject)
                .content(content)
                .build();
        modifyEatTogetherPost.addImages(toTogetherPostImages(eatTogetherPost, images));

        return modifyEatTogetherPost;
    }

    private Set<EatTogetherPostImage> toTogetherPostImages(EatTogetherPost eatTogetherPost, List<String> images) {
        return images.stream()
                .map(v -> EatTogetherPostImage.builder()
                        .eatTogetherPost(eatTogetherPost)
                        .imageUrl(v)
                        .build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
