package com.catholic.hangarae.domain.crawl.service;

import com.catholic.hangarae.domain.crawl.client.GeminiClient;
import com.catholic.hangarae.domain.crawl.client.JevClient;
import com.catholic.hangarae.domain.crawl.client.SchoolNoticeClient;
import com.catholic.hangarae.domain.crawl.dto.response.BoardPageRes;
import com.catholic.hangarae.domain.crawl.dto.response.GeminiDescribeRes;
import com.catholic.hangarae.domain.crawl.dto.response.JevClassifyRes;
import com.catholic.hangarae.domain.crawl.dto.response.SchoolNoticeRes;
import com.catholic.hangarae.domain.department.repository.DepartmentRepository;
import com.catholic.hangarae.global.apiPayLoad.error.CrawlErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.config.GeminiProperties;
import com.catholic.hangarae.global.config.JevProperties;
import com.catholic.hangarae.global.config.SchoolNoticeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolNoticeServiceImpl implements SchoolNoticeService {

    private final SchoolNoticeProperties properties;
    private final JevProperties jevProperties;
    private final GeminiProperties geminiProperties;
    private final SchoolNoticeClient schoolNoticeClient;
    private final JevClient jevClient;
    private final GeminiClient geminiClient;
    private final CollectedNoticeService collectedNoticeService;
    private final DepartmentRepository departmentRepository;
    private final AtomicBoolean running = new AtomicBoolean();

    @Override
    public void collect() {
        // 이전 수집이 끝나지 않았으면 건너뜀
        if (!running.compareAndSet(false, true)) {
            return;
        }
        try {
            for (SchoolNoticeProperties.Source source : properties.getSources()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                collectSource(source);
            }
        } finally {
            running.set(false);
        }
    }

    // 한 게시판의 실패가 다른 게시판 수집을 막지 않음
    private void collectSource(SchoolNoticeProperties.Source source) {
        // 학과 데이터가 없으면 공지 실패가 아닌 설정 오류라 재시도 횟수를 쓰지 않고 건너뜀
        if (source.getDepartmentType() != null
                && departmentRepository.findByName(source.getDepartmentType()).isEmpty()) {
            log.error("학교 공지 게시판 건너뜀, 게시판={}, 오류={}", source.getKey(), CrawlErrorCode.DEPARTMENT_NOT_FOUND.getCode());
            return;
        }
        String url = source.getUrl();
        Set<String> pages = new HashSet<>();
        Set<Long> articles = new HashSet<>();
        int pageCount = 0;
        LocalDate cutoff = LocalDate.now().minusDays(properties.getMaxAgeDays());
        try {
            // 설정한 페이지 수까지 다음 페이지로 이동, 0이면 수집 기간이 끝나는 페이지까지
            while (url != null && pages.add(url)
                    && (properties.getMaxPages() == 0 || pageCount < properties.getMaxPages())) {
                BoardPageRes page = schoolNoticeClient.parseList(schoolNoticeClient.fetch(url));
                boolean hasRecent = false;
                for (Map.Entry<Long, String> article : page.getArticles().entrySet()) {
                    // 고정 공지는 페이지마다 반복되므로 처음 본 글만 기간을 판단
                    if (!articles.add(article.getKey())) {
                        continue;
                    }
                    LocalDate publishedDate = page.getPublishedDates().get(article.getKey());
                    if (publishedDate == null || !publishedDate.isBefore(cutoff)) {
                        hasRecent = true;
                        collectArticle(source, article.getKey(), article.getValue());
                    }
                }
                pageCount++;
                url = hasRecent ? page.getNextUrl() : null;
            }
            log.info("학교 공지 수집 완료, 게시판={}, 페이지={}, 확인 게시글={}", source.getKey(), pageCount, articles.size());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            log.warn("학교 공지 게시판 수집 실패, 게시판={}, 오류={}", source.getKey(), exception.getClass().getSimpleName());
        }
    }

    // 분류까지 성공한 새 공지만 저장, 실패는 DLQ에 기록하고 다음 수집에서 재시도
    private void collectArticle(SchoolNoticeProperties.Source source, Long articleId, String link)
            throws InterruptedException {
        // 이미 저장했거나 재시도를 멈춘 공지는 상세 페이지를 열지 않음
        String url = schoolNoticeClient.canonicalUrl(source, articleId);
        if (!jevProperties.isEnabled() || !collectedNoticeService.isNew(url)) {
            return;
        }
        try {
            SchoolNoticeRes notice = schoolNoticeClient.parseDetail(source, articleId, schoolNoticeClient.fetch(link));
            JevClassifyRes classification = jevClient.classify(notice);
            GeminiDescribeRes description = null;
            if (geminiProperties.isEnabled()) {
                description = geminiClient.describe(notice);
            }
            collectedNoticeService.save(notice, classification, description);
        } catch (InterruptedException exception) {
            throw exception;
        } catch (Exception exception) {
            // 에러 코드가 있으면 코드, 없으면 예외 이름을 실패 사유로 기록
            String reason = exception.getClass().getSimpleName();
            if (exception instanceof BusinessException businessException) {
                reason = businessException.getCode();
            }
            log.warn("학교 공지 처리 실패, 게시판={}, 게시글={}, 오류={}", source.getKey(), articleId, reason);
            collectedNoticeService.fail(url, source.getKey(), reason);
        }
    }
}
