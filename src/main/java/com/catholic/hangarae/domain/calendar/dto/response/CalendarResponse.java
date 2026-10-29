package com.catholic.hangarae.domain.calendar.dto.response;

import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class CalendarResponse {

    // 캘린더에 표시할 일정
    @Getter
    @Builder
    public static class NoticeRes {
        private Long noticeId;
        private String name;
        private String summary;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private Location location;
        private NoticeCategory category;
        private Division division;
    }
}
