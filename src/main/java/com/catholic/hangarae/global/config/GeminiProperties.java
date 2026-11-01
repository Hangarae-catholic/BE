package com.catholic.hangarae.global.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    // 과거 공지 적재 중에는 끄고 JEV 분류만 실행
    private boolean enabled;

    private String apiKey = "";

    private String baseUrl = "https://generativelanguage.googleapis.com";

    private String model = "gemini-3.5-flash-lite";

    @Min(1000)
    private int timeoutMs = 30000;
}
