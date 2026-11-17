package com.catholic.hangarae.domain.calendar.service;

import com.catholic.hangarae.domain.calendar.dto.request.CalendarAcademicReq;
import com.catholic.hangarae.domain.calendar.dto.request.CalendarListReq;
import com.catholic.hangarae.domain.calendar.dto.response.CalendarAcademicRes;
import com.catholic.hangarae.domain.calendar.dto.response.CalendarNoticeRes;
import com.catholic.hangarae.domain.calendar.vo.AcademicSchedule;
import com.catholic.hangarae.domain.notice.entity.Notice;
import com.catholic.hangarae.domain.notice.repository.NoticeRepository;
import com.catholic.hangarae.domain.notice.vo.NoticeType;
import com.catholic.hangarae.domain.user.repository.UserRepository;
import com.catholic.hangarae.global.apiPayLoad.error.CalendarErrorCode;
import com.catholic.hangarae.global.apiPayLoad.error.UserErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CalendarNoticeRes> getCalendar(Long userId, CalendarListReq request) {
        // 탈퇴한 회원은 조회 제외
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        // 계열과 일정 유형 중복 선택 확인
        if (new HashSet<>(request.divisions()).size() != request.divisions().size()
                || new HashSet<>(request.categories()).size() != request.categories().size()) {
            throw new BusinessException(CalendarErrorCode.DUPLICATE_CALENDAR_FILTER);
        }

        YearMonth month = YearMonth.of(request.year(), request.month());
        // 학교 일정 중 선택한 월과 필터에 해당하는 목록
        List<Notice> notices = noticeRepository.findCalendarNotices(
                month.atDay(1).atStartOfDay(), month.plusMonths(1).atDay(1).atStartOfDay(),
                NoticeType.SCHOOL, request.divisions(), request.categories(), userId);

        // 캘린더 응답 생성, 여러 학과 게시판에 올라온 같은 공지는 제목과 기간이 같으면 하나만 표시
        List<CalendarNoticeRes> response = new ArrayList<>();
        Set<String> shown = new HashSet<>();
        for (Notice notice : notices) {
            if (!shown.add(notice.getName() + "|" + notice.getStartAt() + "|" + notice.getEndAt())) {
                continue;
            }
            CalendarNoticeRes item = CalendarNoticeRes.builder()
                    .noticeId(notice.getId())
                    .name(notice.getName())
                    .startAt(notice.getStartAt())
                    .endAt(notice.getEndAt())
                    .location(notice.getLocation())
                    .category(notice.getCategory())
                    .scheduleType(notice.getScheduleType())
                    .division(notice.getDepartment() == null ? null : notice.getDepartment().getDivision())
                    .build();
            response.add(item);
        }
        return response;
    }

    @Override
    public List<CalendarAcademicRes> getAcademicCalendar(CalendarAcademicReq request) {
        YearMonth month = YearMonth.of(request.year(), request.month());
        // 선택한 월과 기간이 하루라도 겹치는 학사일정
        List<CalendarAcademicRes> response = new ArrayList<>();
        for (AcademicSchedule.Item item : AcademicSchedule.ITEMS) {
            if (item.startDate().isAfter(month.atEndOfMonth()) || item.endDate().isBefore(month.atDay(1))) {
                continue;
            }
            response.add(CalendarAcademicRes.builder()
                    .name(item.name())
                    .startDate(item.startDate())
                    .endDate(item.endDate())
                    .build());
        }
        return response;
    }
}
