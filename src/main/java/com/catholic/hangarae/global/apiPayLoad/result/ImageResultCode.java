package com.catholic.hangarae.global.apiPayLoad.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageResultCode implements ResultCode {

    IMAGE_UPLOAD_OK(HttpStatus.OK, "IMAGE001", "이미지 업로드 성공"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
