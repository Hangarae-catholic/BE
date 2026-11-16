package com.catholic.hangarae.domain.crawl.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;

// 게시판 한 페이지의 게시글 링크와 다음 페이지
@Builder
public record BoardPageRes(
        // 게시글 번호별 원문 링크
        Map<Long, String> articles,
        // 게시글 번호별 목록 게시일
        Map<Long, LocalDate> publishedDates,
        String nextUrl
) {
}
