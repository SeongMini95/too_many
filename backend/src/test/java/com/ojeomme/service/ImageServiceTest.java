package com.ojeomme.service;

import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImageServiceTest {

    @InjectMocks
    private ImageService imageService = new ImageService(UPLOAD_PATH, SERVER_HOST, LIMIT_SIZE, new Tika());

    private static final String UPLOAD_PATH = "build/resources/test";
    private static final String SERVER_HOST = "http://localhost:4000";
    private static final String LIMIT_SIZE = "3KB";

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
            assertThat(tempFile.exists()).isTrue();
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
            assertThat(url).startsWith(SERVER_HOST);
            assertThat(url).doesNotContain("temp");
            assertThat(copyFile.exists()).isTrue();
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
            assertThat(copyFile.exists()).isTrue();
        }
    }
}