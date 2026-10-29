package com.catholic.hangarae.global.apiPayLoad.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarResultCode implements ResultCode {

    CALENDAR_LIST_OK(HttpStatus.OK, "CALENDAR001", "캘린더 조회 성공"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
