package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {

    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL001", "이메일 발송에 실패했습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "MAIL002", "인증번호가 틀렸습니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "MAIL003", "인증번호가 만료되었습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "MAIL004", "이메일 인증을 완료해 주세요."),
    VERIFICATION_ATTEMPTS_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "MAIL005", "인증 시도 횟수를 초과했습니다. 인증번호를 다시 받아 주세요."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
