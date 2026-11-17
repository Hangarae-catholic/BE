package com.catholic.hangarae.domain.notice.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import com.catholic.hangarae.domain.notice.vo.NoticeScheduleType;
import com.catholic.hangarae.domain.notice.vo.NoticeType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NoticeDetailRes(
        Long noticeId,
        String name,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Location location,
        String detailLocation,
        String responsible,
        String information,
        String url,
        NoticeType type,
        NoticeCategory category,
        NoticeScheduleType scheduleType,
        boolean approved,
        Division division,
        DepartmentType department,
        List<String> imageUrls
) {
}
