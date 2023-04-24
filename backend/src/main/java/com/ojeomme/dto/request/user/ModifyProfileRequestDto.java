package com.ojeomme.dto.request.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@Getter
public class ModifyProfileRequestDto {

    @URL(message = "프로필 URL 형식이 잘못 되었습니다.")
    private String profile;

    @Builder
    public ModifyProfileRequestDto(String profile) {
        this.profile = profile;
    }
}
