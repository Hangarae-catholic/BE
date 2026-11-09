package com.catholic.hangarae.domain.image.controller;

import com.catholic.hangarae.domain.image.dto.response.ImageUploadRes;
import com.catholic.hangarae.domain.image.service.ImageService;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.ImageResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 API")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = "JPG, PNG, WEBP 파일을 10MB까지 업로드합니다. 받은 URL을 행사, 팀 매칭 등록의 imageUrls에 넣어주세요.")
    public Response<ImageUploadRes> upload(@RequestPart("file") MultipartFile file) {
        return Response.ok(ImageResultCode.IMAGE_UPLOAD_OK, imageService.upload(file));
    }
}
