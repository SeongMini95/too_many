package com.ojeomme.domain.storelikelog;

import com.ojeomme.domain.store.Store;
import com.ojeomme.domain.user.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StoreLikeLogTest {

    @Nested
    class isNew {

        @Test
        void 새로운_entity() {
            // given
            StoreLikeLog storeLikeLog = StoreLikeLog.builder()
                    .store(Store.builder()
                            .id(1L)
                            .build())
                    .user(User.builder()
                            .id(1L)
                            .build())
                    .build();

            // when
            boolean isNew = storeLikeLog.isNew();

            // then
            assertThat(isNew).isTrue();
        }

        @Test
        void 이미_존재하는_entity() {
            // given
            StoreLikeLog storeLikeLog = StoreLikeLog.builder()
                    .store(Store.builder()
                            .id(1L)
                            .build())
                    .user(User.builder()
                            .id(1L)
                            .build())
                    .build();
            storeLikeLog.setDateTime(LocalDateTime.now(), LocalDateTime.now());

            // when
            boolean isNew = storeLikeLog.isNew();

            // then
            assertThat(isNew).isFalse();
        }
    }
}