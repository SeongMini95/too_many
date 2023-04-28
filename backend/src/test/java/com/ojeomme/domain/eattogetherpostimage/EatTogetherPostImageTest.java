package com.ojeomme.domain.eattogetherpostimage;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EatTogetherPostImageTest {

    @Nested
    class testEquals {

        @Test
        void 오브젝트_본인과_비교한다() {
            // given
            EatTogetherPostImage image = EatTogetherPostImage.builder().build();

            // when
            boolean equals = image.equals(image);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 오브젝트가_null이다() {
            // given
            EatTogetherPostImage image = EatTogetherPostImage.builder().build();

            // when
            boolean equals = image.equals(null);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 다른_클래스이다() {
            // given
            EatTogetherPostImage image = EatTogetherPostImage.builder().build();
            Object object = new Object();

            // when
            boolean equals = image.equals(object);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 게시글의_아이디_이미지가_다르다() {
            // given
            EatTogetherPostImage image1 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            EatTogetherPostImage image2 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(2L)
                            .build())
                    .imageUrl("2")
                    .build();

            // when
            boolean equals = image1.equals(image2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디는_같은데_이미지는_다르다() {
            // given
            EatTogetherPostImage image1 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            EatTogetherPostImage image2 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(1L)
                            .build())
                    .imageUrl("2")
                    .build();

            // when
            boolean equals = image1.equals(image2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디는_다르고_이미지는_같다() {
            // given
            EatTogetherPostImage image1 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            EatTogetherPostImage image2 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(2L)
                            .build())
                    .imageUrl("1")
                    .build();

            // when
            boolean equals = image1.equals(image2);

            // then
            assertThat(equals).isFalse();
        }

        @Test
        void 리뷰의_아이디_이미지_모두_같다() {
            // given
            EatTogetherPostImage image1 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            EatTogetherPostImage image2 = EatTogetherPostImage.builder()
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();

            // when
            boolean equals = image1.equals(image2);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 리뷰_이미지의_아이디가_같다() {
            // given
            EatTogetherPostImage image1 = EatTogetherPostImage.builder()
                    .id(1L)
                    .build();
            EatTogetherPostImage image2 = EatTogetherPostImage.builder()
                    .id(1L)
                    .build();

            // when
            boolean equals = image1.equals(image2);

            // then
            assertThat(equals).isTrue();
        }

        @Test
        void 리뷰_이미지의_아이디가_다르다() {
            // given
            EatTogetherPostImage image1 = EatTogetherPostImage.builder()
                    .id(1L)
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(1L)
                            .build())
                    .imageUrl("1")
                    .build();
            EatTogetherPostImage image2 = EatTogetherPostImage.builder()
                    .id(2L)
                    .eatTogetherPost(EatTogetherPost.builder()
                            .id(2L)
                            .build())
                    .imageUrl("2")
                    .build();

            // when
            boolean equals = image1.equals(image2);

            // then
            assertThat(equals).isFalse();
        }
    }

    @Nested
    class testHashCode {

        @Test
        void 해시코드를_가져온다() {
            // given
            EatTogetherPostImage image = EatTogetherPostImage.builder().build();

            // when
            int hashcode = image.hashCode();

            // then
            assertThat(hashcode).isNotZero();
        }
    }
}