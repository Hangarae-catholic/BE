package com.catholic.hangarae.domain.crawl.client;

import com.catholic.hangarae.domain.crawl.dto.response.BoardPageRes;
import com.catholic.hangarae.domain.crawl.dto.response.SchoolNoticeRes;
import com.catholic.hangarae.global.apiPayLoad.error.CrawlErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.config.SchoolNoticeProperties;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SchoolNoticeClient {

    private static final Pattern ARTICLE_ID = Pattern.compile("[?&]articleNo=(\\d+)");
    private static final Pattern OFFSET = Pattern.compile("[?&]article\\.offset=(\\d+)");
    private static final Pattern DATE = Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}");

    private final SchoolNoticeProperties properties;

    // 학교 서버 부하를 줄이기 위해 요청 간격을 둠
    public Document fetch(String url) throws IOException, InterruptedException {
        Thread.sleep(properties.getRequestDelayMs());
        return Jsoup.connect(url)
                .userAgent("HangaraeNoticeCollector/1.0")
                .timeout(properties.getTimeoutMs())
                .maxBodySize(5 * 1024 * 1024)
                .get();
    }

    public BoardPageRes parseList(Document document) {
        // 게시판 목록이 없으면 로그인 페이지 등으로 보고 예외 처리
        Element board = document.selectFirst(".board.list");
        if (board == null) {
            throw new BusinessException(CrawlErrorCode.INVALID_BOARD_CONTENT);
        }

        // 목록형과 이미지형 게시판의 게시글 번호와 원문 링크, 고정 공지 중복은 제외
        Map<Long, String> articles = new LinkedHashMap<>();
        Map<Long, LocalDate> publishedDates = new HashMap<>();
        for (Element link : board.select("a.b-title[href], .bn-list-img01 a[href*='articleNo=']")) {
            Matcher matcher = ARTICLE_ID.matcher(link.attr("href"));
            if (matcher.find()) {
                Long articleId = Long.valueOf(matcher.group(1));
                articles.put(articleId, link.absUrl("href"));
                publishedDates.put(articleId, listDate(link));
            }
        }
        if (articles.isEmpty() && board.selectFirst(".b-no-post") == null) {
            throw new BusinessException(CrawlErrorCode.INVALID_BOARD_CONTENT);
        }

        // 현재 페이지 바로 다음 페이지 링크
        int currentOffset = offset(document.location());
        String nextUrl = null;
        int nextOffset = Integer.MAX_VALUE;
        for (Element link : board.select(".b-paging01 a[href]")) {
            int offset = offset(link.attr("href"));
            if (offset > currentOffset && offset < nextOffset) {
                nextUrl = link.absUrl("href");
                nextOffset = offset;
            }
        }
        return BoardPageRes.builder()
                .articles(articles)
                .publishedDates(publishedDates)
                .nextUrl(nextUrl)
                .build();
    }

    public SchoolNoticeRes parseDetail(SchoolNoticeProperties.Source source, Long articleId, Document document) {
        // 제목과 본문이 없으면 예외 처리
        Element content = document.selectFirst(".board.view .b-content-box");
        Element title = document.select(".board.view .b-title").stream()
                .filter(element -> !element.text().isBlank())
                .findFirst()
                .orElse(null);
        if (content == null || title == null) {
            throw new BusinessException(CrawlErrorCode.INVALID_BOARD_CONTENT);
        }

        // 게시일 확인
        Matcher dateMatcher = DATE.matcher(document.select(".board.view .b-etc-box").text());
        if (!dateMatcher.find()) {
            throw new BusinessException(CrawlErrorCode.INVALID_BOARD_CONTENT);
        }
        LocalDate publishedAt = LocalDate.parse(dateMatcher.group(), DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        // 이미지와 첨부파일은 다운로드하지 않고 본문 뒤에 링크만 보관
        String information = content.wholeText().strip();
        Set<String> resources = new LinkedHashSet<>();
        for (Element image : content.select("img[src]")) {
            resources.add(image.absUrl("src"));
        }
        for (Element link : document.select(".board.view a[href*='mode=download'], .board.view .b-file-box a[href]")) {
            resources.add(link.absUrl("href"));
        }
        if (!resources.isEmpty()) {
            information += "\n\n[원문 이미지, 첨부파일]\n" + String.join("\n", resources);
        }

        // 작성자가 없으면 게시판 이름 사용
        Element writer = document.selectFirst(".board.view .b-writer-box span:not(.title)");
        String writerName = writer == null ? source.getName() : writer.text();

        return SchoolNoticeRes.builder()
                .sourceDepartment(source.getDepartment())
                .departmentType(source.getDepartmentType())
                .name(title.text())
                .information(information)
                .writer(writerName)
                .publishedAt(publishedAt)
                .url(canonicalUrl(source, articleId))
                .build();
    }

    // 목록 링크의 검색 조건을 뺀 원문 주소, 수집 공지의 중복 확인 키
    public String canonicalUrl(SchoolNoticeProperties.Source source, Long articleId) {
        return source.getUrl().split("\\?")[0] + "?mode=view&articleNo=" + articleId;
    }

    // 목록형은 같은 행, 이미지형은 링크 안의 게시일
    private LocalDate listDate(Element link) {
        Element row = link.closest("tr");
        Matcher matcher = DATE.matcher((row == null ? link : row).select(".b-date").text());
        return matcher.find() ? LocalDate.parse(matcher.group(), DateTimeFormatter.ofPattern("yyyy.MM.dd")) : null;
    }

    private int offset(String url) {
        Matcher matcher = OFFSET.matcher(url);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }
}
