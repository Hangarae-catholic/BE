package com.catholic.hangarae.domain.crawl.client;

import com.catholic.hangarae.domain.crawl.dto.request.JevClassifyReq;
import com.catholic.hangarae.domain.crawl.dto.response.JevClassifyRes;
import com.catholic.hangarae.domain.crawl.dto.response.SchoolNoticeRes;
import com.catholic.hangarae.domain.interest.vo.InterestType;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import com.catholic.hangarae.global.apiPayLoad.error.CrawlErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.config.JevProperties;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

// 카테고리와 관심 분야 관련도 분류
@Component
public class JevClient {

    private static final Map<String, String> CATEGORIES = Map.of(
            "GENERAL", "일반 안내, 나머지 분류에 해당하지 않는 공지",
            "DEPARTMENT_ACADEMIC", "수업, 수강, 졸업, 학적, 학사 및 학과 운영",
            "SCHOLARSHIP", "장학금 및 학비 지원",
            "EMPLOYMENT_STARTUP", "채용, 진로, 인턴, 취업 및 창업",
            "CONTEST_COMPETITION", "공모전 및 대회 참가");

    private final JevProperties properties;
    private final RestClient restClient;

    public JevClient(JevProperties properties) {
        this.properties = properties;
        if (properties.isEnabled() && properties.getApiKey().isBlank()) {
            throw new BusinessException(CrawlErrorCode.JEV_API_KEY_REQUIRED);
        }
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getTimeoutMs()))
                .build());
        requestFactory.setReadTimeout(Duration.ofMillis(properties.getTimeoutMs()));
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public JevClassifyRes classify(SchoolNoticeRes notice) {
        if (properties.getApiKey().isBlank()) {
            throw new BusinessException(CrawlErrorCode.JEV_API_KEY_REQUIRED);
        }

        // 공지 원문
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("title", notice.getName());
        state.put("content", notice.getInformation());
        state.put("source_department", Objects.toString(notice.getSourceDepartment(), "학교 공식 홈페이지"));

        // 카테고리 하나와 관심 분야 20개 질문
        Map<String, Object> questions = new LinkedHashMap<>();
        questions.put("category", Map.of("type", "choice",
                "instructions", "이 학교 공지의 주된 내용을 분류하세요.", "criteria", CATEGORIES));
        for (InterestType interest : InterestType.values()) {
            questions.put("interest_" + interest.name(), Map.of("type", "noul", "instructions",
                    "이 공지가 " + interest.name().replace('_', ' ') + " 분야에 관심 있는 학생에게 직접 관련된 기회나 정보를 제공하나요? "
                            + "게시 학과만으로 판단하지 말고 실제 공지 내용으로 판단하세요."));
        }

        JevClassifyReq request = JevClassifyReq.builder()
                .model(properties.getModel())
                .state(state)
                .questions(questions)
                .build();
        JsonNode response;
        try {
            response = restClient.post().uri("/v1/systemone")
                    .headers(headers -> headers.setBearerAuth(properties.getApiKey()))
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new BusinessException(CrawlErrorCode.JEV_REQUEST_FAILED);
        }
        if (response == null || !response.path("answers").isObject()) {
            throw new BusinessException(CrawlErrorCode.INVALID_JEV_RESPONSE);
        }
        JsonNode answers = response.path("answers");

        // 카테고리는 신뢰도가 낮아도 JEV가 고른 값을 사용
        JsonNode category = answers.path("category");
        if (!"choice".equals(category.path("type").asString())
                || !CATEGORIES.containsKey(category.path("choice").asString())) {
            throw new BusinessException(CrawlErrorCode.INVALID_JEV_RESPONSE);
        }

        // 관심 분야별 관련 확률
        Map<InterestType, Double> interests = new EnumMap<>(InterestType.class);
        for (InterestType interest : InterestType.values()) {
            JsonNode answer = answers.path("interest_" + interest.name());
            if (!"noul".equals(answer.path("type").asString())) {
                throw new BusinessException(CrawlErrorCode.INVALID_JEV_RESPONSE);
            }
            interests.put(interest, probability(answer.path("noul")));
        }

        return JevClassifyRes.builder()
                .category(NoticeCategory.valueOf(category.path("choice").asString()))
                .interests(interests)
                .build();
    }

    private double probability(JsonNode node) {
        if (!node.isNumber()) {
            throw new BusinessException(CrawlErrorCode.INVALID_JEV_RESPONSE);
        }
        double value = node.asDouble();
        if (!Double.isFinite(value) || value < 0 || value > 1) {
            throw new BusinessException(CrawlErrorCode.INVALID_JEV_RESPONSE);
        }
        return value;
    }
}
