package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    JWT_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST,"AUTH001", "만료된 jwt 토큰입니다."),
    JWT_INVALID_TOKEN(HttpStatus.BAD_REQUEST,"AUTH002","유효하지 않은 jwt 토큰입니다."),
    JWT_GENERATED_FAILED(HttpStatus.BAD_REQUEST,"AUTH003", "jwt 토큰 생성 실패했습니다."),
    JWT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"AUTH004", "jwt 토큰을 찾을 수 없습니다."),
    AUTHORIZATION_HEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH005", "Authorization 헤더를 찾을 수 없습니다."),
    INVALID_AUTHORIZATION_FORMAT(HttpStatus.BAD_REQUEST, "AUTH006", "유효하지 않은 헤더 포맷입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST,"AUTH007","8~16자의 영문, 숫자, 특수문자를 조합해 주세요"),
    CONFIRM_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST,"AUTH008","비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    INVAILD_EMAIL_FORMAT(HttpStatus.BAD_REQUEST,"AUTH009","이메일 형식이 일치하지 않습니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"AUTH010","이미 존재하는 이메일입니다."),
    PASSWORD_UNMATCH_ERROR(HttpStatus.BAD_REQUEST,"AUTH011", "아이디, 비밀번호가 일치하지 않습니다."),
    LOGIN_ID_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"AUTH012", "이미 존재하는 아이디입니다."),
    NICKNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"AUTH013", "사용 불가능한 닉네임입니다"),
    INVALID_LOGIN_ID_FORMAT(HttpStatus.BAD_REQUEST,"AUTH014", "올바른 아이디 형식이 아닙니다"),
    JWT_CONFIGURATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"AUTH015", "JWT 설정이 올바르지 않습니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST,"AUTH016", "올바른 닉네임 형식이 아닙니다"),
    LOGIN_RESTRICTED(HttpStatus.TOO_MANY_REQUESTS,"AUTH017", "로그인 시도 횟수를 초과했습니다. 비밀번호를 재설정해 주세요."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
