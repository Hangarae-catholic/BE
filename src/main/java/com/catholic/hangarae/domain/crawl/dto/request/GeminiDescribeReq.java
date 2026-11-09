package com.catholic.hangarae.domain.crawl.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

// Gemini Interactions 요청
@Getter
@Builder
public class GeminiDescribeReq {

    private String model;

    private String input;

    @JsonProperty("generation_config")
    private Map<String, String> generationConfig;

    // JSON 스키마로 응답 형식 지정
    @JsonProperty("response_format")
    private Map<String, Object> responseFormat;
}
