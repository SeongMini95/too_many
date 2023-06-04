package com.ojeomme.domain.eattogetherreply;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EatTogetherReplyTest {

    @Nested
    class isNew {

        @Test
        void 새로운_객체이다() {
            // given
            EatTogetherReply eatTogetherReply = EatTogetherReply.builder().build();

            // when
            boolean isNew = eatTogetherReply.isNew();

            // then
            assertThat(isNew).isTrue();
        }

        @Test
        void 새로운_객체가_아니다() {
            // given
            EatTogetherReply eatTogetherReply = EatTogetherReply.builder().build();
            eatTogetherReply.setDateTime(LocalDateTime.now(), LocalDateTime.now());

            // when
            boolean isNew = eatTogetherReply.isNew();

            // then
            assertThat(isNew).isFalse();
        }
    }

    @Nested
    class modifyContent {

        @Test
        void 내용을_수정한다() {
            // given
            EatTogetherReply eatTogetherReply = EatTogetherReply.builder()
                    .content("수정 전")
                    .build();

            // when
            eatTogetherReply.modifyContent("수정 후");

            // then
            assertThat(eatTogetherReply.getContent()).isEqualTo("수정 후");
        }
    }

    @Nested
    class modifyImageUrl {

        @Test
        void 이미지를_수정한다() {
            // given
            EatTogetherReply eatTogetherReply = EatTogetherReply.builder()
                    .imageUrl("image")
                    .build();

            // when
            eatTogetherReply.modifyImageUrl("change");

            // then
            assertThat(eatTogetherReply.getImageUrl()).isEqualTo("change");
        }
    }

    @Nested
    class delete {

        @Test
        void 댓글을_삭제한다() {
            // given
            EatTogetherReply eatTogetherReply = EatTogetherReply.builder()
                    .deleteYn(false)
                    .build();

            // when
            eatTogetherReply.delete();

            // then
            assertThat(eatTogetherReply.isDeleteYn()).isTrue();
        }
    }
}