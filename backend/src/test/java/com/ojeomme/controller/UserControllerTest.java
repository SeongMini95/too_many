package com.ojeomme.controller;

import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.dto.request.user.ModifyMyInfoRequestDto;
import com.ojeomme.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    @Nested
    class modifyMyInfo {

        @Test
        void 내_정보를_수정한다() throws Exception {
            // given
            createImage();

            String originalProfile = user.getProfile();

            ModifyMyInfoRequestDto requestDto = ModifyMyInfoRequestDto.builder()
                    .nickname("변경")
                    .profile("http://localhost:4000/temp/2023/6/5/image1.png")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/user/my")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("nickname")).isEqualTo(requestDto.getNickname());
            assertThat(jsonPath.getString("profile")).isNotEqualTo(originalProfile);
        }

        @Test
        void 기본_프로필로_변경한다() {
            // given
            ModifyMyInfoRequestDto requestDto = ModifyMyInfoRequestDto.builder()
                    .nickname("변경")
                    .profile("")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/user/my")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getString("nickname")).isEqualTo(requestDto.getNickname());
            assertThat(jsonPath.getString("profile")).isBlank();
        }

        @Test
        void 내_정보를_수정하는데_유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            ModifyMyInfoRequestDto requestDto = ModifyMyInfoRequestDto.builder()
                    .nickname("변경")
                    .profile("")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/user/my")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }

        private void createImage() throws Exception {
            String uploadPath = "build/resources/test";

            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            File tempFile = Paths.get(uploadPath, "/temp/2023/6/5/image1.png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();
        }
    }

    @Nested
    class getMyInfo {

        @Test
        void 내_정보를_가져온다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
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
                    .when().get("/api/user/my")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }
    }
}