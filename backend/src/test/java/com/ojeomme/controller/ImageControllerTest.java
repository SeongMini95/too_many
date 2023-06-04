package com.ojeomme.controller;

import com.ojeomme.common.jwt.entity.AuthToken;
import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.exception.ApiErrorCode;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ImageControllerTest extends AcceptanceTest {

    @Nested
    class tempUploadEditor {

        @Value("${security.jwt.token.secret-key}")
        private String secretKey;

        @Test
        void 이미지를_임시폴더에_업로드한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("upload", "test.png", outputStream.toByteArray())
                    .when().post("/api/image/upload/editor")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("url")).isNotBlank();
        }

        @Test
        void 용량을_초과하면_ImageSizeLimitExceededException를_발생한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("upload", "test.png", outputStream.toByteArray())
                    .when().post("/api/image/upload/editor")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("error.message")).isEqualTo(ApiErrorCode.IMAGE_SIZE_LIMIT_EXCEEDED.getMessage());
        }

        @Test
        void 이미지_형식이_아니면_ImageMimeTypeException_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("upload", "test.text", "텍스트".getBytes())
                    .when().post("/api/image/upload/editor")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("error.message")).isEqualTo(ApiErrorCode.IMAGE_MIME_TYPE.getMessage());
        }

        @Test
        void 토큰이_존재하지_않는다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("upload", "test.png", outputStream.toByteArray())
                    .when().post("/api/image/upload/editor")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("error.message")).isEqualTo(ApiErrorCode.UNAUTHORIZED.getMessage());
        }

        @Test
        void 토큰이_만료됐지만_존재하는_유저() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            AuthToken authToken = new AuthToken(Keys.hmacShaKeyFor(secretKey.getBytes()), 1L, -1);
            String accessToken = authToken.getToken();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("upload", "test.png", outputStream.toByteArray())
                    .when().post("/api/image/upload/editor")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("url")).isNotBlank();
        }

        @Test
        void 토큰의_유저가_없을_경우() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2("aaa")
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("upload", "test.png", outputStream.toByteArray())
                    .when().post("/api/image/upload/editor")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(jsonPath.getString("error.message")).isEqualTo(ApiErrorCode.UNAUTHORIZED.getMessage());
        }
    }

    @Nested
    class tempUpload {

        @Test
        void 이미지를_임시폴더에_업로드한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("image", "test.png", outputStream.toByteArray())
                    .when().post("/api/image/upload")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 용량을_초과하면_ImageSizeLimitExceededException를_발생한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("image", "test.png", outputStream.toByteArray())
                    .when().post("/api/image/upload")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.IMAGE_SIZE_LIMIT_EXCEEDED.getHttpStatus().value());
        }

        @Test
        void 이미지_형식이_아니면_ImageMimeTypeException_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .multiPart("image", "test.text", "텍스트".getBytes())
                    .when().post("/api/image/upload")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.IMAGE_MIME_TYPE.getHttpStatus().value());
        }
    }
}