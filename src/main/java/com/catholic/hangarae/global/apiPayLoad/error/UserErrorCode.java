package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"USER001","사용자를 찾을 수 없습니다"),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "USER002", "올바른 닉네임 형식이 아닙니다"),
    NICKNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER003", "사용 불가능한 닉네임입니다"),
    INVALID_DEPARTMENTS(HttpStatus.BAD_REQUEST, "USER004", "제1전공 1개를 포함해 전공은 최대 3개, 계열은 최대 2개 선택해 주세요"),
    INVALID_INTERESTS(HttpStatus.BAD_REQUEST, "USER005", "관심 분야는 중복 없이 1개부터 4개까지 선택해 주세요"),
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER006", "학과를 찾을 수 없습니다"),
    INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND, "USER007", "관심 분야를 찾을 수 없습니다"),
    PROFILE_ALREADY_EXIST(HttpStatus.CONFLICT, "USER008", "이미 생성된 프로필입니다"),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER009", "프로필을 먼저 생성해 주세요"),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
