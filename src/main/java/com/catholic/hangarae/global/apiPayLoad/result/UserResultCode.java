package com.catholic.hangarae.global.apiPayLoad.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserResultCode implements ResultCode {

    USER_SIGNUP_OK(HttpStatus.CREATED,"AUTH001", "유저 회원가입 성공"),
    USER_LOGIN_OK(HttpStatus.OK,"AUTH002", "유저 로그인 성공"),
    USER_LOGOUT_OK(HttpStatus.OK,"AUTH003", "유저 로그아웃 성공"),
    USER_WITHDRAW_OK(HttpStatus.OK,"AUTH004", "유저 회원탈퇴 성공"),
    USER_PROFILE_CREATE_OK(HttpStatus.CREATED, "USER001", "프로필 생성 성공"),
    USER_PROFILE_GET_OK(HttpStatus.OK, "USER002", "프로필 조회 성공"),
    USER_PROFILE_UPDATE_OK(HttpStatus.OK, "USER003", "프로필 수정 성공"),
    USER_NICKNAME_CHECK_OK(HttpStatus.OK, "USER004", "사용 가능한 닉네임입니다"),
    USER_PROFILE_OPTIONS_OK(HttpStatus.OK, "USER005", "프로필 선택 항목 조회 성공"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
