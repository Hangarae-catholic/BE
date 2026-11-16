package com.catholic.hangarae.domain.crawl.dto.response;

import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeScheduleType;
import lombok.Builder;

import java.time.LocalDate;

// Gemini 요약 결과
@Builder
public record GeminiDescribeRes(
        String information,
        LocalDate startDate,
        LocalDate endDate,
        Location location,
        // 호실, 층 등 상세 주소
        String detailLocation,
        NoticeScheduleType scheduleType,
        // 주최기관, 본문에 없으면 작성자
        String responsible
) {
}
