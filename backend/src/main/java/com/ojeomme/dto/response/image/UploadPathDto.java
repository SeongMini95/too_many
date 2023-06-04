package com.ojeomme.dto.response.image;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class UploadPathDto {

    private final Path savePath;
    private final String saveUrl;

    public UploadPathDto(Path uploadPath, String host, String filename, boolean temp) {
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
