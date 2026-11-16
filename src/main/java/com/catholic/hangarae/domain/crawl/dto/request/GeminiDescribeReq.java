package com.catholic.hangarae.domain.crawl.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Map;

// Gemini Interactions 요청
@Builder
public record GeminiDescribeReq(
        String model,
        String input,
        @JsonProperty("generation_config") Map<String, String> generationConfig,
        // JSON 스키마로 응답 형식 지정
        @JsonProperty("response_format") Map<String, Object> responseFormat
) {
}
