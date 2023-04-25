package com.ojeomme.domain.eattogetherpost;

import com.ojeomme.domain.eattogetherpostimage.EatTogetherPostImage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EatTogetherPostTest {

    @Nested
    class addImages {

        @Test
        void 이미지를_추가한다() {
            // given
            EatTogetherPost eatTogetherPost = EatTogetherPost.builder().build();

            Set<EatTogetherPostImage> images = Set.of(
                    EatTogetherPostImage.builder().eatTogetherPost(eatTogetherPost).imageUrl("image1").build(),
                    EatTogetherPostImage.builder().eatTogetherPost(eatTogetherPost).imageUrl("image2").build(),
                    EatTogetherPostImage.builder().eatTogetherPost(eatTogetherPost).imageUrl("image3").build()
            );

            // when
            eatTogetherPost.addImages(images);

            // then
            assertThat(eatTogetherPost.getImages()).isEqualTo(images);
        }
    }
}