package com.catholic.hangarae.domain.crawl.dto.response;

import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeScheduleType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// Gemini 요약 결과
@Getter
@Builder
public class GeminiDescribeRes {

    private String information;
    private LocalDate startDate;
    private LocalDate endDate;
    private Location location;
    // 호실, 층 등 상세 주소
    private String detailLocation;
    private NoticeScheduleType scheduleType;

    // 주최기관, 본문에 없으면 작성자
    private String responsible;
}
