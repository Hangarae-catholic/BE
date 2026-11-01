package com.catholic.hangarae.domain.crawl.dto.response;

import com.catholic.hangarae.domain.interest.vo.InterestType;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

// JEV 분류 결과
@Getter
@Builder
public class JevClassifyRes {

    private NoticeCategory category;

    // 관심 분야별 관련 확률
    private Map<InterestType, Double> interests;
}
