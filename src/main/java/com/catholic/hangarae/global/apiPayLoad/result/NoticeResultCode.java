package com.catholic.hangarae.global.apiPayLoad.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NoticeResultCode implements ResultCode {

    NOTICE_LIST_OK(HttpStatus.OK, "NOTICE001", "행사 목록 조회 성공"),
    NOTICE_DETAIL_OK(HttpStatus.OK, "NOTICE002", "행사 상세 조회 성공"),
    NOTICE_CREATE_OK(HttpStatus.OK, "NOTICE003", "사설 행사 등록 성공"),
    NOTICE_DELETE_OK(HttpStatus.OK, "NOTICE004", "사설 행사 삭제 성공"),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
