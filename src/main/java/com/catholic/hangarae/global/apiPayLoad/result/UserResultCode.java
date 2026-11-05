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

    USER_KAKAO_SIGNUP_OK(HttpStatus.CREATED, "AUTH005", "카카오 회원가입 성공"),
    USER_KAKAO_LOGIN_OK(HttpStatus.OK, "AUTH006", "카카오 로그인 성공"),

    USER_GOOGLE_SIGNUP_OK(HttpStatus.CREATED, "AUTH007", "구글 회원가입 성공"),
    USER_GOOGLE_LOGIN_OK(HttpStatus.OK, "AUTH008", "구글 로그인 성공"),

    USER_LOGIN_ID_CHECK_OK(HttpStatus.OK, "AUTH009", "사용 가능한 아이디입니다."),
    USER_VERIFICATION_CODE_SENT(HttpStatus.OK, "AUTH010", "인증번호 발송 성공"),
    USER_EMAIL_VERIFIED(HttpStatus.OK, "AUTH011", "인증되었습니다."),
    USER_TOKEN_REISSUE_OK(HttpStatus.OK, "AUTH012", "토큰 재발급 성공"),
    USER_FIND_ID_OK(HttpStatus.OK, "AUTH013", "아이디 찾기 성공"),
    USER_PASSWORD_RESET_OK(HttpStatus.OK, "AUTH014", "비밀번호 재설정 성공"),

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
