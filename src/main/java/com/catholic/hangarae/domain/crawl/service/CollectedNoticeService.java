package com.catholic.hangarae.domain.crawl.service;

import com.catholic.hangarae.domain.crawl.dto.response.GeminiDescribeRes;
import com.catholic.hangarae.domain.crawl.dto.response.JevClassifyRes;
import com.catholic.hangarae.domain.crawl.dto.response.SchoolNoticeRes;

public interface CollectedNoticeService {

    boolean isNew(String url);

    Long save(SchoolNoticeRes data, JevClassifyRes classification, GeminiDescribeRes description);

    void fail(String url, String sourceKey, String reason);
}
