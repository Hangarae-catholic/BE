package com.catholic.hangarae.domain.calendar.dto.request;

import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CalendarListReq(
        // 조회 연도와 월
        @NotNull @Min(1) @Max(9999) Integer year,
        @NotNull @Min(1) @Max(12) Integer month,
        // 선택 계열, 1~2개
        @NotNull @Size(min = 1, max = 2) List<@NotNull Division> divisions,
        // 일정 유형, 1~2개
        @NotNull @Size(min = 1, max = 2) List<@NotNull NoticeCategory> categories
) {
}
