package com.catholic.hangarae.domain.image.service;

import com.catholic.hangarae.domain.image.dto.response.ImageUploadRes;
import com.catholic.hangarae.global.apiPayLoad.error.ImageErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3Client s3Client;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.public-url}")
    private String publicUrl;

    @Override
    public ImageUploadRes upload(MultipartFile file) {
        byte[] content = readContent(file);

        // 클라이언트가 보낸 형식은 위조할 수 있어 실제 파일 내용으로 판별
        String extension = detectExtension(content);
        if (extension == null) {
            throw new BusinessException(ImageErrorCode.INVALID_IMAGE_FILE);
        }

        String key = "images/" + UUID.randomUUID() + "." + extension;
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("image/" + (extension.equals("jpg") ? "jpeg" : extension))
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(content));
        } catch (SdkException e) {
            log.error("이미지 업로드 실패", e);
            throw new BusinessException(ImageErrorCode.IMAGE_UPLOAD_FAILED);
        }

        return new ImageUploadRes(publicUrl + "/" + key);
    }

    private byte[] readContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BusinessException(ImageErrorCode.INVALID_IMAGE_FILE);
        }
    }

    // 파일 시그니처로 형식 판별, 스크립트를 담을 수 있는 SVG는 허용하지 않음
    private String detectExtension(byte[] c) {
        if (c.length >= 3 && (c[0] & 0xFF) == 0xFF && (c[1] & 0xFF) == 0xD8 && (c[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (c.length >= 8 && (c[0] & 0xFF) == 0x89 && c[1] == 'P' && c[2] == 'N' && c[3] == 'G'
                && c[4] == 0x0D && c[5] == 0x0A && c[6] == 0x1A && c[7] == 0x0A) {
            return "png";
        }
        if (c.length >= 12 && c[0] == 'R' && c[1] == 'I' && c[2] == 'F' && c[3] == 'F'
                && c[8] == 'W' && c[9] == 'E' && c[10] == 'B' && c[11] == 'P') {
            return "webp";
        }
        return null;
    }
}
