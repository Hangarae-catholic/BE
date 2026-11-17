package com.catholic.hangarae.domain.calendar.dto.response;

import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import com.catholic.hangarae.domain.notice.vo.NoticeScheduleType;
import lombok.Builder;

import java.time.LocalDateTime;

// 캘린더에 표시할 일정
@Builder
public record CalendarNoticeRes(
        Long noticeId,
        String name,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Location location,
        NoticeCategory category,
        NoticeScheduleType scheduleType,
        Division division
) {
}
