package com.catholic.hangarae.domain.crawl.dto.response;

import com.catholic.hangarae.domain.interest.vo.InterestType;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import lombok.Builder;

import java.util.Map;

// JEV 분류 결과
@Builder
public record JevClassifyRes(
        NoticeCategory category,
        // 관심 분야별 관련 확률
        Map<InterestType, Double> interests
) {
}
