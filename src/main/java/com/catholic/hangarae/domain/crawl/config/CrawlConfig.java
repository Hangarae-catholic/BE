package com.catholic.hangarae.domain.crawl.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableConfigurationProperties({SchoolNoticeProperties.class, JevProperties.class})
public class CrawlConfig {

    // 수집 중에도 기존 팀 마감 스케줄러가 실행되도록 분리
    @Bean
    @ConditionalOnProperty(name = "school-notice.enabled", havingValue = "true")
    public ThreadPoolTaskScheduler taskScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("hangarae-schedule-");
        return scheduler;
    }
}
