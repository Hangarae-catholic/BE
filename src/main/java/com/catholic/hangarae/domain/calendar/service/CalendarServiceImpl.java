package com.catholic.hangarae.domain.calendar.service;

import com.catholic.hangarae.domain.calendar.dto.request.CalendarRequest;
import com.catholic.hangarae.domain.calendar.dto.response.CalendarResponse;
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

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CalendarResponse.NoticeRes> getCalendar(Long userId, CalendarRequest.ListReq request) {
        // 탈퇴한 회원은 조회 제외
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        // 계열과 일정 유형 중복 선택 확인
        if (new HashSet<>(request.getDivisions()).size() != request.getDivisions().size()
                || new HashSet<>(request.getCategories()).size() != request.getCategories().size()) {
            throw new BusinessException(CalendarErrorCode.DUPLICATE_CALENDAR_FILTER);
        }

        YearMonth month = YearMonth.of(request.getYear(), request.getMonth());
        // 학교 일정 중 선택한 월과 필터에 해당하는 목록
        List<Notice> notices = noticeRepository.findCalendarNotices(
                month.atDay(1).atStartOfDay(), month.plusMonths(1).atDay(1).atStartOfDay(),
                NoticeType.SCHOOL, request.getDivisions(), request.getCategories(), userId);

        // 캘린더 응답 생성
        List<CalendarResponse.NoticeRes> response = new ArrayList<>();
        for (Notice notice : notices) {
            CalendarResponse.NoticeRes item = CalendarResponse.NoticeRes.builder()
                    .noticeId(notice.getId())
                    .name(notice.getName())
                    .summary(notice.getSummary())
                    .startAt(notice.getStartAt())
                    .endAt(notice.getEndAt())
                    .location(notice.getLocation())
                    .category(notice.getCategory())
                    .division(notice.getDepartment() == null ? null : notice.getDepartment().getDivision())
                    .build();
            response.add(item);
        }
        return response;
    }
}
