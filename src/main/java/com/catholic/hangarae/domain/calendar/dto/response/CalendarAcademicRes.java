package com.catholic.hangarae.domain.calendar.dto.response;

import lombok.Builder;

import java.time.LocalDate;

// 학사일정
@Builder
public record CalendarAcademicRes(
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
}
