package com.catholic.hangarae.domain.crawl.service;

import com.catholic.hangarae.global.config.SchoolNoticeProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "school-notice.enabled", havingValue = "true")
public class SchoolNoticeScheduler {

    private final SchoolNoticeService schoolNoticeService;
    private final SchoolNoticeProperties properties;
    private final TaskScheduler taskScheduler;

    // 기본 매일 0시, 12시 수집
    @Scheduled(cron = "${school-notice.cron:0 0 0,12 * * *}", zone = "Asia/Seoul")
    public void collect() {
        schoolNoticeService.collect();
    }

    // 과거 공지 적재처럼 바로 수집이 필요하면 시작 직후 한 번 실행, 수집 중 돌아온 정기 수집은 건너뜀
    @EventListener(ApplicationReadyEvent.class)
    public void collectOnStartup() {
        if (properties.isRunOnStartup()) {
            taskScheduler.schedule(schoolNoticeService::collect, Instant.now());
        }
    }
}
