package com.ojeomme.domain.eattogetherreply;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EatTogetherReplyTest {

    @Nested
    class isNew {

        @Test
        void 새로운_entity() {
            // given
            EatTogetherReply eatTogetherReply = EatTogetherReply.builder().build();

            // when
            boolean isNew = eatTogetherReply.isNew();

            // then
            assertThat(isNew).isTrue();
        }

        @Test
        void 이미_존재하는_entity() {
            // given
            EatTogetherReply eatTogetherReply = EatTogetherReply.builder().build();
            eatTogetherReply.setDateTime(LocalDateTime.now(), LocalDateTime.now());

            // when
            boolean isNew = eatTogetherReply.isNew();

            // then
            assertThat(isNew).isFalse();
        }
    }
}