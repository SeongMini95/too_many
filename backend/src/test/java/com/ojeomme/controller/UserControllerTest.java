package com.ojeomme.controller;

import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.user.ModifyNicknameRequestDto;
import com.ojeomme.dto.request.user.ModifyProfileRequestDto;
import com.ojeomme.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    class getMyInfo {

        @Test
        void 내_정보를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/api/user/my")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("provider")).isEqualTo(user.getOauthProvider().getCode());
            assertThat(jsonPath.getString("nickname")).isEqualTo(user.getNickname());
            assertThat(jsonPath.getString("email")).isEqualTo(user.getEmail());
            assertThat(jsonPath.getString("profile")).isEqualTo(user.getProfile());
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/api/user/my")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class modifyNickname {

        private final ModifyNicknameRequestDto requestDto = ModifyNicknameRequestDto.builder()
                .nickname("change123")
                .build();

        @Test
        void 닉네임을_변경한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/user/my/nickname")
                    .then().log().all()
                    .extract();

            User getUser = userRepository.findAll().get(0);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(getUser.getNickname()).isEqualTo(requestDto.getNickname());
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/user/my/nickname")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class modifyProfile {

        @Test
        void 프로필을_변경한다() throws Exception {
            // given
            createImage();

            String originalProfile = user.getProfile();

            ModifyProfileRequestDto requestDto = ModifyProfileRequestDto.builder()
                    .profile("http://localhost:4000/temp/2023/4/24/image1.png")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/user/my/profile")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.asString()).isNotEqualTo(originalProfile);
        }
    }

    @Test
    void 기본_프로필로_변경한다() {
        // given
        ModifyProfileRequestDto requestDto = ModifyProfileRequestDto.builder()
                .profile("")
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDto)
                .when().put("/api/user/my/profile")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        assertThat(response.asString()).isBlank();
    }

    @Test
    void 유저가_없으면_UserNotFoundException를_발생한다() {
        // given
        ModifyProfileRequestDto requestDto = ModifyProfileRequestDto.builder()
                .profile("http://localhost:4000/temp/2023/4/24/image1.png")
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .auth().oauth2(notExistAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestDto)
                .when().put("/api/user/my/profile")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
        assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
    }

    private void createImage() throws Exception {
        String uploadPath = "build/resources/test";

        BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        File tempFile = Paths.get(uploadPath, "/temp/2023/4/24/image1.png").toFile();
        Files.createDirectories(tempFile.getParentFile().toPath());

        FileOutputStream outputStream = new FileOutputStream(tempFile);
        ImageIO.write(bufferedImage, "png", outputStream);
        outputStream.close();
    }
}