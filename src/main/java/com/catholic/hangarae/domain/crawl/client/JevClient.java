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
    // 관심 분야 이름과 예시, 영어 enum 이름보다 한국어 공지와 잘 맞음
    private static final Map<InterestType, String> INTERESTS = new EnumMap<>(Map.ofEntries(
            Map.entry(InterestType.BUSINESS_ACCOUNTING_FINANCE, "경영, 회계, 금융 (재무, 세무, 투자, 은행)"),
            Map.entry(InterestType.IT_SOFTWARE, "IT, 소프트웨어 (개발, 코딩, 앱, 웹)"),
            Map.entry(InterestType.AI_DATA, "AI, 데이터 (인공지능 활용, 데이터 분석, 빅데이터)"),
            Map.entry(InterestType.BIO_LIFE_SCIENCE, "바이오, 생명과학 (생명공학, 의생명, 실험 연구)"),
            Map.entry(InterestType.MEDICAL_HEALTH_PHARMACY, "의료, 보건, 약학 (병원, 간호, 제약, 건강)"),
            Map.entry(InterestType.CHEMISTRY_ENERGY_ENVIRONMENT, "화학, 에너지, 환경 (화학공학, 신재생에너지, 기후)"),
            Map.entry(InterestType.SEMICONDUCTOR_ELECTRONICS_COMMUNICATION, "반도체, 전자, 통신 (회로, 정보통신, 하드웨어)"),
            Map.entry(InterestType.ECONOMICS_TRADE_INTERNATIONAL, "경제, 무역, 국제 (국제통상, 해외 교류, 국제기구)"),
            Map.entry(InterestType.MARKETING_ADVERTISING_PR, "마케팅, 광고, 홍보 (브랜딩, SNS 홍보, 서포터즈)"),
            Map.entry(InterestType.PLANNING_PM, "기획, PM (서비스 기획, 프로젝트 관리, 행사 기획)"),
            Map.entry(InterestType.DESIGN_FASHION_SPACE, "디자인, 패션, 공간 (시각 디자인, 의류, 인테리어)"),
            Map.entry(InterestType.CONTENT_MEDIA_ART, "콘텐츠, 미디어, 예술 (영상, 사진, 음악, 공연, 전시, 글쓰기)"),
            Map.entry(InterestType.EDUCATION_TEACHING, "교육, 교직 (교사, 교직 이수, 멘토링, 교육 봉사)"),
            Map.entry(InterestType.LAW_POLICY, "법률, 정책 (법학, 인권, 정책 제안)"),
            Map.entry(InterestType.ADMINISTRATION_PUBLIC_INSTITUTION, "행정, 공공기관 (공무원, 공기업, 지자체 사업)"),
            Map.entry(InterestType.SOCIETY_WELFARE_PSYCHOLOGY, "사회, 복지, 심리 (사회복지, 상담, 봉사활동)"),
            Map.entry(InterestType.HUMANITIES_HISTORY_PHILOSOPHY, "인문, 역사, 철학 (문학, 종교, 인문학 강연)"),
            Map.entry(InterestType.FOREIGN_LANGUAGE, "외국어, 언어 (어학, 통번역, 교환학생, 어학연수)"),
            Map.entry(InterestType.CERTIFICATION_EXAM, "자격증, 시험 (어학 시험, 자격시험, 고시)"),
            Map.entry(InterestType.EMPLOYMENT_STARTUP_EXTERNAL_ACTIVITY, "취업, 창업, 대외활동 (채용, 추천채용, 취업 특강, 인턴, 창업, 서포터즈, 공모전)")));

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
                    "이 공지가 " + INTERESTS.get(interest) + " 분야에 관심 있는 학생에게 직접 관련된 기회나 정보를 제공하나요? "
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
