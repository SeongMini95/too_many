package com.ojeomme.controller;

import com.ojeomme.common.jwt.handler.AccessTokenExtractor;
import com.ojeomme.dto.response.image.EditorImageUrlResponseDto;
import com.ojeomme.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> tempUpload(@RequestPart MultipartFile image) throws IOException {
        String url = imageService.tempUpload(image);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/upload/editor")
    public ResponseEntity<EditorImageUrlResponseDto> tempUploadEditor(HttpServletRequest request, @RequestPart MultipartFile upload) throws IOException {
        EditorImageUrlResponseDto responseDto = imageService.tempUploadEditor(AccessTokenExtractor.extract(request).orElse(""), upload);
        return ResponseEntity.ok(responseDto);
    }
}
