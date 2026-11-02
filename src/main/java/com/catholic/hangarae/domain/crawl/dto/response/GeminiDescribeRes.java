package com.catholic.hangarae.domain.crawl.dto.response;

import com.catholic.hangarae.domain.notice.vo.Location;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// Gemini 요약 결과
@Getter
@Builder
public class GeminiDescribeRes {

    private String summary;
    private String information;
    private LocalDate startDate;
    private LocalDate endDate;
    private Location location;

    // 주최기관, 본문에 없으면 작성자
    private String responsible;
}
