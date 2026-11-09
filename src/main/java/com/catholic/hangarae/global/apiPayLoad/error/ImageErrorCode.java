package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "IMAGE001", "JPG, PNG, WEBP 이미지만 업로드할 수 있습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_GATEWAY, "IMAGE002", "이미지 업로드에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
