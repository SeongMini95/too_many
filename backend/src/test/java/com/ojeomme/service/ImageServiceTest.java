package com.ojeomme.service;

import com.ojeomme.common.jwt.entity.AuthToken;
import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.dto.response.image.EditorImageUrlResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import io.jsonwebtoken.security.Keys;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageServiceTest {

    private final ImageService imageService = new ImageService(UPLOAD_PATH, SERVER_HOST, LIMIT_SIZE, new Tika(), new AuthTokenProvider(KEY, 100000));

    private static final String UPLOAD_PATH = "build/resources/test";
    private static final String SERVER_HOST = "http://localhost:4000";
    private static final String LIMIT_SIZE = "3KB";
    private static final String KEY = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Nested
    class tempUploadEditor {

        private final AuthToken authToken = new AuthToken(Keys.hmacShaKeyFor(KEY.getBytes()), 1L, 100000);
        private final AuthToken expiredAuthToken = new AuthToken(Keys.hmacShaKeyFor(KEY.getBytes()), 1L, -1000);
        private final String accessToken = authToken.getToken();

        @Test
        void 이미지를_임시폴더에_업로드한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", outputStream.toByteArray());
            outputStream.close();

            // when
            EditorImageUrlResponseDto responseDto = imageService.tempUploadEditor(accessToken, image);

            URL fileUrl = new URL(responseDto.getUrl());
            File tempFile = Paths.get(UPLOAD_PATH, fileUrl.getPath()).toFile();

            // then
            assertThat(responseDto.getUrl()).startsWith(SERVER_HOST);
            assertThat(tempFile).exists();
        }

        @Test
        void 용량을_초과하면_ImageSizeLimitExceededException를_발생한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", outputStream.toByteArray());
            outputStream.close();

            // when
            EditorImageUrlResponseDto responseDto = imageService.tempUploadEditor(accessToken, image);

            // then
            assertThat(responseDto.getError().getMessage()).isEqualTo(ApiErrorCode.IMAGE_SIZE_LIMIT_EXCEEDED.getMessage());
        }

        @Test
        void 이미지_형식이_아니면_ImageMimeTypeException_발생한다() throws IOException {
            // given
            MultipartFile notImage = new MockMultipartFile("notImage", "notImage.txt", "plain/text", "notImage".getBytes());

            // when
            EditorImageUrlResponseDto responseDto = imageService.tempUploadEditor(accessToken, notImage);

            // then
            assertThat(responseDto.getError().getMessage()).isEqualTo(ApiErrorCode.IMAGE_MIME_TYPE.getMessage());
        }

        @Test
        void 토큰이_존재하지_않는다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", outputStream.toByteArray());
            outputStream.close();

            String accessToken = "";

            // when
            EditorImageUrlResponseDto responseDto = imageService.tempUploadEditor(accessToken, image);

            // then
            assertThat(responseDto.getError().getMessage()).isEqualTo(ApiErrorCode.UNAUTHORIZED.getMessage());
        }

        @Test
        void 토큰이_만료됐지만_존재하는_유저() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", outputStream.toByteArray());
            outputStream.close();

            String accessToken = expiredAuthToken.getToken();

            // when
            EditorImageUrlResponseDto responseDto = imageService.tempUploadEditor(accessToken, image);

            URL fileUrl = new URL(responseDto.getUrl());
            File tempFile = Paths.get(UPLOAD_PATH, fileUrl.getPath()).toFile();

            // then
            assertThat(responseDto.getUrl()).startsWith(SERVER_HOST);
            assertThat(tempFile).exists();
        }

        @Test
        void 토큰의_유저가_없을_경우() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", outputStream.toByteArray());
            outputStream.close();

            String accessToken = "wrong";

            // when
            EditorImageUrlResponseDto responseDto = imageService.tempUploadEditor(accessToken, image);

            // then
            assertThat(responseDto.getError().getMessage()).isEqualTo(ApiErrorCode.UNAUTHORIZED.getMessage());
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

            MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", outputStream.toByteArray());
            outputStream.close();

            // when
            String url = imageService.tempUpload(image);

            URL fileUrl = new URL(url);
            File tempFile = Paths.get(UPLOAD_PATH, fileUrl.getPath()).toFile();

            // then
            assertThat(url).startsWith(SERVER_HOST);
            assertThat(tempFile).exists();
        }

        @Test
        void 용량을_초과하면_ImageSizeLimitExceededException를_발생한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);

            MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", outputStream.toByteArray());
            outputStream.close();

            // when
            ApiException exception = assertThrows(ApiException.class, () -> imageService.tempUpload(image));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.IMAGE_SIZE_LIMIT_EXCEEDED);
        }

        @Test
        void 이미지_형식이_아니면_ImageMimeTypeException_발생한다() {
            // given
            MultipartFile notImage = new MockMultipartFile("notImage", "notImage.txt", "plain/text", "notImage".getBytes());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> imageService.tempUpload(notImage));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.IMAGE_MIME_TYPE);
        }
    }

    @Nested
    class copyImage {

        @Test
        void 임시폴더의_이미지를_저장폴더로_복사한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            File tempFile = Paths.get(UPLOAD_PATH, "/temp/copyImage.png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();

            String tempUrl = UriComponentsBuilder.fromHttpUrl(SERVER_HOST)
                    .pathSegment("temp", "copyImage.png")
                    .toUriString();

            // when
            String url = imageService.copyImage(tempUrl);

            URL fileUrl = new URL(url);
            File copyFile = Paths.get(UPLOAD_PATH, fileUrl.getPath()).toFile();

            // then
            assertThat(url).startsWith(SERVER_HOST).doesNotContain("temp");
            assertThat(copyFile).exists();
        }

        @Test
        void URL이_잘못되면_ImageHostNotSupportException를_발생한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            File tempFile = Paths.get(UPLOAD_PATH, "/temp/copyImage.png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();

            String tempUrl = UriComponentsBuilder.fromHttpUrl("http://notLocalhost:4000")
                    .pathSegment("temp", "copyImage.png")
                    .toUriString();

            // when
            ApiException exception = assertThrows(ApiException.class, () -> imageService.copyImage(tempUrl));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.IMAGE_HOST_NOT_SUPPORT);
        }

        @Test
        void temp_이미지가_아니면_그대로_반환한다() throws Exception {
            // given
            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            String pathName = "/2023/4/14/" + UUID.randomUUID() + ".png";
            File tempFile = Paths.get(UPLOAD_PATH, pathName).toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();

            String tempUrl = UriComponentsBuilder.fromHttpUrl(SERVER_HOST)
                    .path(pathName)
                    .toUriString();

            // when
            String url = imageService.copyImage(tempUrl);

            URL fileUrl = new URL(url);
            File copyFile = Paths.get(UPLOAD_PATH, fileUrl.getPath()).toFile();

            // then
            assertThat(url).isEqualTo(SERVER_HOST + pathName);
            assertThat(copyFile).exists();
        }
    }
}