package com.catholic.hangarae.global.apiPayLoad.result;

import org.springframework.http.HttpStatus;

public interface ResultCode {

    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
