package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamErrorCode implements ErrorCode {

    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND,"TEAM001","존재하지 않는 팀 매칭입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
