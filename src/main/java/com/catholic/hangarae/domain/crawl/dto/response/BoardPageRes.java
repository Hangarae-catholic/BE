package com.catholic.hangarae.domain.crawl.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

// 게시판 한 페이지의 게시글 링크와 다음 페이지
@Getter
@Builder
public class BoardPageRes {

    // 게시글 번호별 원문 링크
    private Map<Long, String> articles;

    private String nextUrl;
}
