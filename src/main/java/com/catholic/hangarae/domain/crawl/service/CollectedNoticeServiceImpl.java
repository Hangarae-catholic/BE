package com.catholic.hangarae.domain.crawl.service;

import com.catholic.hangarae.domain.crawl.dto.response.GeminiDescribeRes;
import com.catholic.hangarae.domain.crawl.dto.response.JevClassifyRes;
import com.catholic.hangarae.domain.crawl.dto.response.SchoolNoticeRes;
import com.catholic.hangarae.domain.crawl.entity.NoticeFailure;
import com.catholic.hangarae.domain.crawl.repository.NoticeFailureRepository;
import com.catholic.hangarae.domain.department.entity.Department;
import com.catholic.hangarae.domain.department.repository.DepartmentRepository;
import com.catholic.hangarae.domain.interest.entity.Interest;
import com.catholic.hangarae.domain.interest.repository.InterestRepository;
import com.catholic.hangarae.domain.notice.entity.Notice;
import com.catholic.hangarae.domain.notice.entity.NoticeInterest;
import com.catholic.hangarae.domain.notice.repository.NoticeInterestRepository;
import com.catholic.hangarae.domain.notice.repository.NoticeRepository;
import com.catholic.hangarae.domain.notice.vo.NoticeType;
import com.catholic.hangarae.global.apiPayLoad.error.CrawlErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class CollectedNoticeServiceImpl implements CollectedNoticeService {

    // 세 번 실패한 공지는 DLQ에 남겨 수동 처리
    private static final int MAX_ATTEMPTS = 3;

    private final NoticeRepository noticeRepository;
    private final NoticeInterestRepository noticeInterestRepository;
    private final NoticeFailureRepository noticeFailureRepository;
    private final DepartmentRepository departmentRepository;
    private final InterestRepository interestRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isNew(String url) {
        // 저장된 공지는 다시 수집 x
        if (noticeRepository.existsByTypeAndUrl(NoticeType.SCHOOL, url)) {
            return false;
        }
        return noticeFailureRepository.findByUrl(url)
                .map(failure -> failure.getAttempts() < MAX_ATTEMPTS)
                .orElse(true);
    }

    // 분류에 성공한 공지만 저장
    @Override
    @Transactional
    public Long save(SchoolNoticeRes data, JevClassifyRes classification, GeminiDescribeRes description) {
        // 학과 게시판 공지는 해당 학과 대상, 학교 홈페이지 공지는 전교생 대상
        Department department = null;
        if (data.getDepartmentType() != null) {
            department = departmentRepository.findByName(data.getDepartmentType())
                    .orElseThrow(() -> new BusinessException(CrawlErrorCode.DEPARTMENT_NOT_FOUND));
        }

        // 원문 정보로 공지 생성, 제미나이 결과가 있으면 요약 정보로 채움
        Notice.NoticeBuilder builder = Notice.builder()
                .type(NoticeType.SCHOOL)
                .url(data.getUrl())
                .name(data.getName())
                .information(data.getInformation())
                .responsible(data.getWriter())
                .category(classification.getCategory())
                .department(department);
        if (description != null) {
            builder.information(description.getInformation())
                    .responsible(description.getResponsible())
                    .location(description.getLocation())
                    .scheduleType(description.getScheduleType());
            if (description.getStartDate() != null) {
                builder.startAt(description.getStartDate().atStartOfDay())
                        .endAt(description.getEndDate().atTime(LocalTime.of(23, 59, 59)));
            }
        }
        Notice notice = builder.build();
        // 학교 공식 게시판 원문이라 승인된 행사로 표시
        notice.approve();
        noticeRepository.save(notice);

        // 추천 순위에 쓰도록 관심 분야 점수는 모두 저장
        for (Interest interest : interestRepository.findAll()) {
            Double score = classification.getInterests().get(interest.getName());
            if (score != null) {
                NoticeInterest noticeInterest = NoticeInterest.builder()
                        .notice(notice)
                        .interest(interest)
                        .score(score)
                        .build();
                noticeInterestRepository.save(noticeInterest);
            }
        }

        // 재시도에 성공하면 실패 기록 삭제
        noticeFailureRepository.deleteByUrl(data.getUrl());
        return notice.getId();
    }

    @Override
    @Transactional
    public void fail(String url, String sourceKey, String reason) {
        // 처음 실패한 공지면 기록 생성
        NoticeFailure failure = noticeFailureRepository.findByUrl(url).orElse(null);
        if (failure == null) {
            failure = noticeFailureRepository.save(NoticeFailure.builder()
                    .url(url)
                    .sourceKey(sourceKey)
                    .build());
        }
        failure.fail(reason);
    }
}
