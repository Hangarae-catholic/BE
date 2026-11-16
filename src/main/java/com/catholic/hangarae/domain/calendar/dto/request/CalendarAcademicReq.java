package com.catholic.hangarae.domain.calendar.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CalendarAcademicReq(
        // 조회 연도와 월
        @NotNull @Min(1) @Max(9999) Integer year,
        @NotNull @Min(1) @Max(12) Integer month
) {
}
