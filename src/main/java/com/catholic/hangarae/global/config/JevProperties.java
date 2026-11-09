package com.catholic.hangarae.global.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jev")
public class JevProperties {

    private boolean enabled;

    private String apiKey = "";

    private String baseUrl = "https://api.typesafe.ai";

    private String model = "jev-latest";

    @Min(1000)
    private int timeoutMs = 30000;
}
