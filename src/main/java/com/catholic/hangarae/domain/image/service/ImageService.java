package com.catholic.hangarae.domain.image.service;

import com.catholic.hangarae.domain.image.dto.response.ImageUploadRes;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageUploadRes upload(MultipartFile file);
}
