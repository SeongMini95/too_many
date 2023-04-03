package com.toomany.domain.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Nested
    class updateEmail {

        @Test
        void 이메일을_업데이트한다() {
            // given
            User user = User.builder()
                    .email("email@email.com")
                    .build();

            // when
            user.updateEmail("change@email.com");

            // then
            assertThat(user.getEmail()).isEqualTo("change@email.com");
        }
    }
}