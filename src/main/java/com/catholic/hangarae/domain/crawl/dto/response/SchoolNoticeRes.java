package com.catholic.hangarae.domain.crawl.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// 게시판 상세에서 읽은 원문
@Getter
@Builder
public class SchoolNoticeRes {

    // 게시한 학과 이름, 학교 홈페이지 공지는 null
    private String sourceDepartment;

    // 학과 게시판이면 대상 학과로 사용
    private DepartmentType departmentType;

    private String name;

    // 본문과 이미지, 첨부파일 링크
    private String information;

    private String writer;
    private LocalDate publishedAt;
    private String url;
}
