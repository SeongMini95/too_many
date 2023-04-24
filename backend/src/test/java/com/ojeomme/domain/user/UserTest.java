package com.ojeomme.domain.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Nested
    class modifyNickname {

        @Test
        void 닉네임을_변경한다() {
            // given
            User user = User.builder()
                    .nickname("test123")
                    .build();

            // when
            user.modifyNickname("change123");

            // then
            assertThat(user.getNickname()).isEqualTo("change123");
        }
    }

    @Nested
    class modifyProfile {

        @Test
        void 프로필을_변경한다() {
            // given
            User user = User.builder()
                    .profile("http://localhost:4000/profile.png")
                    .build();

            // when
            user.modifyProfile("http://localhost:4000/change.png");

            // then
            assertThat(user.getProfile()).isEqualTo("http://localhost:4000/change.png");
        }
    }

    @Nested
    class defaultProfile {

        @Test
        void 기본_이미지로_변경한다() {
            // given
            User user = User.builder()
                    .profile("http://localhost:4000/profile.png")
                    .build();

            // when
            user.setDefaultProfile();

            // then
            assertThat(user.getProfile()).isBlank();
        }
    }

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