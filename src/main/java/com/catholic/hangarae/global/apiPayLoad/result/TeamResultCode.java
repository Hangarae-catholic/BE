package com.catholic.hangarae.global.apiPayLoad.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TeamResultCode implements ResultCode {

    TEAM_CREATE_OK(HttpStatus.OK, "TEAM001", "팀 매칭 생성 성공"),
    TEAM_LIST_OK(HttpStatus.OK, "TEAM002", "팀 매칭 목록 조회 성공"),
    TEAM_DETAIL_OK(HttpStatus.OK, "TEAM003", "팀 매칭 상세 조회 성공"),
    TEAM_SEARCH_OK(HttpStatus.OK, "TEAM004", "팀 매칭 목록 검색 성공"),
    TEAM_DELETE_OK(HttpStatus.OK, "TEAM005", "팀 매칭 삭제 성공"),
    TEAM_UPDATE_OK(HttpStatus.OK, "TEAM006", "팀 매칭 수정 성공"),

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
