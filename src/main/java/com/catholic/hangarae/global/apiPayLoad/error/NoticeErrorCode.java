package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode implements ErrorCode {

    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE001", "행사를 찾을 수 없습니다"),
    INVALID_NOTICE_DATE(HttpStatus.BAD_REQUEST, "NOTICE002", "오늘부터 7일 이내의 날짜를 선택해 주세요"),
    PRIVATE_NOTICE_ONLY(HttpStatus.FORBIDDEN, "NOTICE003", "사설 행사만 삭제 가능합니다."),
    WRITER_DIFFERENCE(HttpStatus.FORBIDDEN, "NOTICE004", "글의 작성자만 수정 또는 삭제할 수 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
