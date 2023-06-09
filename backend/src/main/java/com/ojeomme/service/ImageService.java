package com.ojeomme.service;

import com.ojeomme.common.jwt.entity.AuthToken;
import com.ojeomme.common.jwt.handler.AuthTokenProvider;
import com.ojeomme.dto.response.image.EditorImageUrlResponseDto;
import com.ojeomme.dto.response.image.UploadPathDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final Path uploadPath;
    private final String host;
    private final long limitSize;
    private final Tika tika;
    private final AuthTokenProvider authTokenProvider;

    public ImageService(@Value("${image.upload-path}") String uploadPath,
                        @Value("${image.host}") String host,
                        @Value("${image.limit-size}") String limitSize,
                        Tika tika,
                        AuthTokenProvider authTokenProvider) {
        this.uploadPath = Paths.get(uploadPath);
        this.host = host;
        this.limitSize = DataSize.parse(limitSize).toBytes();
        this.tika = tika;
        this.authTokenProvider = authTokenProvider;
    }

    public String tempUpload(MultipartFile image) throws IOException {
        if (limitSize < image.getSize()) {
            throw new ApiException(ApiErrorCode.IMAGE_SIZE_LIMIT_EXCEEDED.setMessageVars(DataSize.ofBytes(limitSize).toMegabytes()));
        }

        String mimeType = tika.detect(image.getInputStream());
        if (!mimeType.startsWith("image")) {
            throw new ApiException(ApiErrorCode.IMAGE_MIME_TYPE);
        }

        UploadPathDto uploadPathDto = new UploadPathDto(this.uploadPath, host, image.getOriginalFilename(), true);
        Path savePath = uploadPathDto.getSavePath();
        Files.createDirectories(savePath.getParent());
        image.transferTo(savePath);

        return uploadPathDto.getSaveUrl();
    }

    @Transactional
    public String copyImage(String tempUrl) throws IOException {
        // 이미지 호스트
        URL hostUrl = new URL(host);
        String imageHost = hostUrl.getHost();

        // 파라미터의 호스트
        URL url = new URL(tempUrl);
        String path = url.getPath().substring(1);
        String tempHost = url.getHost();

        if (imageHost.equals(tempHost)) {
            if (path.startsWith("temp")) {
                Path tempFilePath = uploadPath.resolve(path);

                UploadPathDto uploadPathDto = new UploadPathDto(this.uploadPath, host, tempFilePath.getFileName().toString(), false);
                Path savePath = uploadPathDto.getSavePath();
                Files.createDirectories(savePath.getParent());

                FileUtils.copyFile(tempFilePath.toFile(), savePath.toFile());

                return uploadPathDto.getSaveUrl();
            } else {
                return tempUrl;
            }
        } else {
            throw new ApiException(ApiErrorCode.IMAGE_HOST_NOT_SUPPORT);
        }
    }

    @Transactional
    public EditorImageUrlResponseDto tempUploadEditor(String accessToken, MultipartFile upload) throws IOException {
        EditorImageUrlResponseDto responseDto;

        try {
            // 토큰 없으면
            if (StringUtils.isBlank(accessToken)) {
                throw new ApiException(ApiErrorCode.UNAUTHORIZED);
            }

            // 토큰의 유저가 존재하지 않으면
            AuthToken authToken = authTokenProvider.convertAuthToken(accessToken);
            if (authToken.getUserId() == null && authToken.getExpiredUserId() == null) {
                throw new ApiException(ApiErrorCode.UNAUTHORIZED);
            }

            responseDto = EditorImageUrlResponseDto.success(tempUpload(upload));
        } catch (ApiException e) {
            responseDto = EditorImageUrlResponseDto.fail(e.getMessage());
        }

        return responseDto;
    }
}
