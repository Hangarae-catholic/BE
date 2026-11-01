package com.catholic.hangarae.domain.crawl.client;

import com.catholic.hangarae.domain.crawl.dto.request.GeminiDescribeReq;
import com.catholic.hangarae.domain.crawl.dto.response.GeminiDescribeRes;
import com.catholic.hangarae.domain.crawl.dto.response.SchoolNoticeRes;
import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.global.apiPayLoad.error.CrawlErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.config.GeminiProperties;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// 공지 요약, 상세 내용, 일정, 장소, 주최기관 생성
@Component
public class GeminiClient {

    private static final String NONE = "NONE";
    private static final Map<Location, String> BUILDINGS = new EnumMap<>(Map.ofEntries(
            Map.entry(Location.KIM_SOU_HWAN_HALL, "김수환관"), Map.entry(Location.MARIA_HALL, "마리아관"),
            Map.entry(Location.NICHOLLS_HALL, "니콜스관"), Map.entry(Location.BAMBINO_HALL, "밤비노관"),
            Map.entry(Location.DASOL_HALL, "다솔관"), Map.entry(Location.VIRTUS_HALL, "비르투스관"),
            Map.entry(Location.STUDENT_HALL, "학생회관"), Map.entry(Location.MICHAEL_HALL, "미카엘관"),
            Map.entry(Location.VERITAS_HALL, "중앙도서관(베리타스관)"), Map.entry(Location.SACRED_HEART_HALL, "성심관"),
            Map.entry(Location.PHARMACY_HALL, "약학관"), Map.entry(Location.CONCERT_HALL, "콘서트홀"),
            Map.entry(Location.CHURCH, "성당"), Map.entry(Location.STEPHANO_DORMITORY, "스테파노 기숙사"),
            Map.entry(Location.ANDREA_DORMITORY, "안드레아 기숙사")));

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiProperties properties;
    private final RestClient restClient;

    public GeminiClient(GeminiProperties properties) {
        this.properties = properties;
        if (properties.isEnabled() && properties.getApiKey().isBlank()) {
            throw new BusinessException(CrawlErrorCode.GEMINI_API_KEY_REQUIRED);
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

    public GeminiDescribeRes describe(SchoolNoticeRes notice) {
        if (properties.getApiKey().isBlank()) {
            throw new BusinessException(CrawlErrorCode.GEMINI_API_KEY_REQUIRED);
        }

        // 구조화 출력 요청
        GeminiDescribeReq request = GeminiDescribeReq.builder()
                .model(properties.getModel())
                .input(prompt(notice))
                .generationConfig(Map.of("thinking_level", "minimal"))
                .responseFormat(Map.of("type", "text", "mime_type", "application/json", "schema", schema()))
                .build();
        JsonNode response;
        try {
            response = restClient.post().uri("/v1beta/interactions")
                    .header("x-goog-api-key", properties.getApiKey())
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException exception) {
            throw new BusinessException(CrawlErrorCode.GEMINI_REQUEST_FAILED);
        }
        JsonNode result = parse(response);

        // 요약이 비어 있으면 실패로 처리
        String summary = result.path("summary").asString().strip();
        if (summary.isBlank()) {
            throw new BusinessException(CrawlErrorCode.INVALID_GEMINI_RESPONSE);
        }
        String information = result.path("information").asString().strip();
        String responsible = result.path("responsible").asString().strip();

        // 날짜가 불완전하거나 역전되면 일정으로 노출하지 않음
        LocalDate startDate = date(result.path("start_date").asString());
        LocalDate endDate = date(result.path("end_date").asString());
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            startDate = null;
            endDate = null;
        }

        // 건물 목록에 없는 장소는 지도에 표시하지 않음
        String locationName = result.path("location").asString();
        Location location = BUILDINGS.keySet().stream()
                .filter(item -> item.name().equals(locationName))
                .findFirst()
                .orElse(null);

        return GeminiDescribeRes.builder()
                .summary(cut(summary, 255))
                .information(information.isBlank() ? notice.getInformation() : information)
                .startDate(startDate)
                .endDate(endDate)
                .location(location)
                .responsible(cut(responsible.isBlank() ? notice.getWriter() : responsible, 255))
                .build();
    }

    private String prompt(SchoolNoticeRes notice) {
        StringBuilder buildings = new StringBuilder();
        BUILDINGS.forEach((location, name) -> buildings.append(location.name()).append('=').append(name).append(", "));
        return """
                가톨릭대학교 성심교정 공지를 학생용 캘린더와 지도에 표시할 정보로 정리하세요.
                - summary: 공지 내용을 한 문장으로 소개, 60자 이내
                - information: 학생에게 필요한 내용(대상, 일정, 신청 방법, 문의처)을 500자 이내로 정리, 원문에 없는 내용은 쓰지 않음
                - start_date, end_date: 공지의 주된 행사나 신청 기간, yyyy-MM-dd 형식. 하루 일정은 같은 날짜. 연도가 없으면 게시일 기준으로 판단. 없으면 빈 문자열
                - location: 행사 장소가 아래 건물이면 해당 값, 온라인이거나 교외이거나 명시되지 않으면 NONE. 접수처나 문의처 위치는 장소가 아님
                  %s
                - responsible: 주최 또는 주관 기관, 본문에 없으면 빈 문자열

                제목: %s
                게시일: %s
                게시 학과: %s
                작성자: %s
                본문:
                %s
                """.formatted(buildings, notice.getName(), notice.getPublishedAt(),
                Objects.toString(notice.getSourceDepartment(), "학교 공식 홈페이지"), notice.getWriter(),
                notice.getInformation());
    }

    // 응답 JSON 스키마, nullable 대신 빈 문자열과 NONE 사용
    private Map<String, Object> schema() {
        List<String> locations = new ArrayList<>(List.of(NONE));
        BUILDINGS.keySet().forEach(location -> locations.add(location.name()));
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("summary", Map.of("type", "string"));
        fields.put("information", Map.of("type", "string"));
        fields.put("start_date", Map.of("type", "string"));
        fields.put("end_date", Map.of("type", "string"));
        fields.put("location", Map.of("type", "string", "enum", locations));
        fields.put("responsible", Map.of("type", "string"));
        return Map.of("type", "object", "properties", fields, "required", List.copyOf(fields.keySet()));
    }

    // 응답의 model_output 단계에 있는 JSON 텍스트
    private JsonNode parse(JsonNode response) {
        if (response != null) {
            for (JsonNode step : response.path("steps")) {
                if (!"model_output".equals(step.path("type").asString())) {
                    continue;
                }
                for (JsonNode content : step.path("content")) {
                    if ("text".equals(content.path("type").asString())) {
                        try {
                            JsonNode result = objectMapper.readTree(content.path("text").asString());
                            if (result.isObject()) {
                                return result;
                            }
                        } catch (JacksonException exception) {
                            throw new BusinessException(CrawlErrorCode.INVALID_GEMINI_RESPONSE);
                        }
                    }
                }
            }
        }
        throw new BusinessException(CrawlErrorCode.INVALID_GEMINI_RESPONSE);
    }

    private LocalDate date(String value) {
        try {
            return value == null || value.isBlank() ? null : LocalDate.parse(value.strip());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private String cut(String value, int length) {
        return value == null || value.length() <= length ? value : value.substring(0, length);
    }
}
