package com.catholic.hangarae.domain.crawl.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

// JEV System One 요청
@Getter
@Builder
public class JevClassifyReq {

    private String model;

    // 공지 제목, 본문, 게시 학과
    private Map<String, Object> state;

    // 질문 이름별 Choice, Noul 질문
    private Map<String, Object> questions;
}
