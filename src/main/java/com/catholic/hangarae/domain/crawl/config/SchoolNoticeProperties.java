package com.catholic.hangarae.domain.crawl.config;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "school-notice")
public class SchoolNoticeProperties {

    // 0이면 마지막 페이지까지 수집
    @Min(0)
    private int maxPages = 3;

    @Min(100)
    private long requestDelayMs = 500;

    @Min(1000)
    private int timeoutMs = 15000;

    @Valid
    private List<Source> sources = new ArrayList<>();

    @Getter
    @Setter
    public static class Source {
        @NotBlank
        @Size(max = 120)
        private String key;
        @NotBlank
        private String name;
        @NotBlank
        @Pattern(regexp = "https://[A-Za-z0-9.-]*catholic\\.ac\\.kr/.*")
        private String url;
        private String department;
        // 비어 있으면 JEV가 대상 학과를 판단
        private DepartmentType departmentType;
    }
}
