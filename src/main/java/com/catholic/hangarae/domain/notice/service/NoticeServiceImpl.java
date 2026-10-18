package com.catholic.hangarae.domain.notice.service;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.dto.response.NoticeDetailRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeListRes;
import com.catholic.hangarae.domain.notice.entity.Notice;
import com.catholic.hangarae.domain.notice.repository.NoticeImageRepository;
import com.catholic.hangarae.domain.notice.repository.NoticeRepository;
import com.catholic.hangarae.global.apiPayLoad.error.NoticeErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NoticeListRes> getNotices(LocalDate date) {
        // 날짜를 선택하지 않으면 오늘로 설정
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate selectedDate = date;
        if (selectedDate == null) {
            selectedDate = today;
        }
        // 오늘부터 7일 이내인지 확인
        if (selectedDate.isBefore(today) || selectedDate.isAfter(today.plusDays(6))) {
            throw new BusinessException(NoticeErrorCode.INVALID_NOTICE_DATE);
        }

        // 선택한 날짜의 행사 조회
        List<Notice> notices = noticeRepository.findByDate(
                selectedDate.atStartOfDay(), selectedDate.plusDays(1).atStartOfDay());

        // 목록 응답 생성
        List<NoticeListRes> response = new ArrayList<>();
        for (Notice notice : notices) {
            NoticeListRes item = NoticeListRes.builder()
                    .noticeId(notice.getId())
                    .name(notice.getName())
                    .startAt(notice.getStartAt())
                    .endAt(notice.getEndAt())
                    .location(notice.getLocation())
                    .summary(notice.getSummary())
                    .approved(notice.isApproved())
                    .build();
            response.add(item);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDetailRes getNotice(Long noticeId) {
        // 행사 조회, 없으면 예외 처리
        Notice notice = noticeRepository.findById(noticeId).orElse(null);
        if (notice == null) {
            throw new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        // 학과가 있으면 계열과 학과 정보 설정
        Division division = null;
        DepartmentType department = null;
        if (notice.getDepartment() != null) {
            division = notice.getDepartment().getDivision();
            department = notice.getDepartment().getName();
        }

        // 행사 이미지 URL 조회
        List<String> imageUrls = noticeImageRepository.findAllByNoticeIdOrderByIdAsc(noticeId)
                .stream()
                .map(noticeImage -> noticeImage.getImage().getUrl())
                .toList();

        // 상세 응답 생성
        NoticeDetailRes response = NoticeDetailRes.builder()
                .noticeId(notice.getId())
                .name(notice.getName())
                .summary(notice.getSummary())
                .startAt(notice.getStartAt())
                .endAt(notice.getEndAt())
                .location(notice.getLocation())
                .responsible(notice.getResponsible())
                .information(notice.getInformation())
                .url(notice.getUrl())
                .type(notice.getType())
                .category(notice.getCategory())
                .approved(notice.isApproved())
                .division(division)
                .department(department)
                .imageUrls(imageUrls)
                .build();
        return response;
    }
}
