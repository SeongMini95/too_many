package com.ojeomme.service;

import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ImageService {

    private final Path uploadPath;
    private final String host;
    private final long limitSize;
    private final Tika tika;

    public ImageService(@Value("${image.upload-path}") String uploadPath,
                        @Value("${image.host}") String host,
                        @Value("${image.limit-size}") String limitSize,
                        Tika tika) {
        this.uploadPath = Paths.get(uploadPath);
        this.host = host;
        this.limitSize = DataSize.parse(limitSize).toBytes();
        this.tika = tika;
    }

    public String tempUpload(MultipartFile image) throws IOException {
        if (limitSize < image.getSize()) {
            throw new ApiException(ApiErrorCode.IMAGE_SIZE_LIMIT_EXCEEDED.setMessageVars(DataSize.ofBytes(limitSize).toMegabytes()));
        }

        String mimeType = tika.detect(image.getInputStream());
        if (!mimeType.startsWith("image")) {
            throw new ApiException(ApiErrorCode.IMAGE_MIME_TYPE);
        }

        UploadPath uploadPath = new UploadPath(this.uploadPath, host, image.getOriginalFilename(), true);
        Path savePath = uploadPath.getSavePath();
        Files.createDirectories(savePath.getParent());
        image.transferTo(savePath);

        return uploadPath.getSaveUrl();
    }

    @Transactional
    public String copyImage(String tempUrl) throws IOException {
        URL url = new URL(tempUrl);
        String path = url.getPath().substring(1);
        String fullUrl = String.format("%s://%s:%d", url.getProtocol(), url.getHost(), url.getPort());

        if (fullUrl.equals(host)) {
            if (path.startsWith("temp")) {
                Path tempFilePath = uploadPath.resolve(path);

                UploadPath uploadPath = new UploadPath(this.uploadPath, host, tempFilePath.getFileName().toString(), false);
                Path savePath = uploadPath.getSavePath();
                Files.createDirectories(savePath.getParent());

                FileUtils.copyFile(tempFilePath.toFile(), savePath.toFile());

                return uploadPath.getSaveUrl();
            } else {
                return tempUrl;
            }
        } else {
            throw new ApiException(ApiErrorCode.IMAGE_HOST_NOT_SUPPORT);
        }
    }

    @Getter
    public static class UploadPath {

        private final Path savePath;
        private final String saveUrl;

        public UploadPath(Path uploadPath, String host, String filename, boolean temp) {
            SecureRandom random = new SecureRandom();
            String ext = FilenameUtils.getExtension(filename);
            String randomFilename = new BigInteger(30, random) + "_" + UUID.randomUUID() + "." + ext;

            LocalDate now = LocalDate.now();
            String year = now.getYear() + "";
            String month = now.getMonthValue() + "";
            String day = now.getDayOfMonth() + "";

            if (temp) {
                this.savePath = uploadPath.resolve("temp").resolve(year).resolve(month).resolve(day).resolve(randomFilename);
                this.saveUrl = UriComponentsBuilder.fromHttpUrl(host)
                        .pathSegment("temp", year, month, day, randomFilename)
                        .toUriString();
            } else {
                this.savePath = uploadPath.resolve(year).resolve(month).resolve(day).resolve(randomFilename);
                this.saveUrl = UriComponentsBuilder.fromHttpUrl(host)
                        .pathSegment(year, month, day, randomFilename)
                        .toUriString();
            }
        }
    }
}
