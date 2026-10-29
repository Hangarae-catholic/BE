package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode implements ErrorCode {

    DUPLICATE_CALENDAR_FILTER(HttpStatus.BAD_REQUEST, "CALENDAR001", "계열과 일정 유형은 중복 없이 선택해 주세요"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
