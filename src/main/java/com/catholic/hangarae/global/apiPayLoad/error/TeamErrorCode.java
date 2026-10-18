package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamErrorCode implements ErrorCode {

    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND,"TEAM001","팀 매칭 정보를 찾을 수 없습니다."),
    WRITER_DIFFERENCE(HttpStatus.FORBIDDEN, "TEAM002", "팀 매칭 글의 작성자만 수정 또는 삭제할 수 있습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
