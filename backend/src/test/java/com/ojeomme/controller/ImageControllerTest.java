package com.ojeomme.controller;

import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ImageControllerTest extends AcceptanceTest {

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