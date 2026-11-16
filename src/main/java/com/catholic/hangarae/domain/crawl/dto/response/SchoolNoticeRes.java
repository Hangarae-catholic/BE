package com.catholic.hangarae.domain.crawl.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import lombok.Builder;

import java.time.LocalDate;

// 게시판 상세에서 읽은 원문
@Builder
public record SchoolNoticeRes(
        // 게시한 학과 이름, 학교 홈페이지 공지는 null
        String sourceDepartment,
        // 학과 게시판이면 대상 학과로 사용
        DepartmentType departmentType,
        String name,
        // 본문과 이미지, 첨부파일 링크
        String information,
        String writer,
        LocalDate publishedAt,
        String url
) {
}
