package com.catholic.hangarae.global.apiPayLoad.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CrawlErrorCode implements ErrorCode {

    INVALID_BOARD_CONTENT(HttpStatus.BAD_GATEWAY, "CRAWL001", "학교 공지 페이지 형식을 확인해 주세요"),
    JEV_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "CRAWL002", "JEV 분류 요청에 실패했습니다"),
    INVALID_JEV_RESPONSE(HttpStatus.BAD_GATEWAY, "CRAWL003", "JEV 분류 결과가 올바르지 않습니다"),
    JEV_API_KEY_REQUIRED(HttpStatus.INTERNAL_SERVER_ERROR, "CRAWL004", "JEV API 키를 설정해 주세요"),
    GEMINI_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "CRAWL005", "Gemini 요약 요청에 실패했습니다"),
    INVALID_GEMINI_RESPONSE(HttpStatus.BAD_GATEWAY, "CRAWL006", "Gemini 요약 결과가 올바르지 않습니다"),
    GEMINI_API_KEY_REQUIRED(HttpStatus.INTERNAL_SERVER_ERROR, "CRAWL007", "Gemini API 키를 설정해 주세요"),
    DEPARTMENT_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "CRAWL008", "게시판 학과가 학과 목록에 없습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
