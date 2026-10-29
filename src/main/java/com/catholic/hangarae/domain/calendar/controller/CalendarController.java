package com.catholic.hangarae.domain.calendar.controller;

import com.catholic.hangarae.domain.calendar.dto.request.CalendarRequest;
import com.catholic.hangarae.domain.calendar.dto.response.CalendarResponse;
import com.catholic.hangarae.domain.calendar.service.CalendarService;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.CalendarResultCode;
import com.catholic.hangarae.global.security.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "캘린더 조회 API")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    @Operation(summary = "월별 캘린더 조회", description = "계열과 일정 유형을 각각 1~2개 선택 후 조회합니다. 상세는 /api/notices/{noticeId}로 조회")
    public Response<List<CalendarResponse.NoticeRes>> getCalendar(
            @AuthUser Long userId, @Valid @ModelAttribute CalendarRequest.ListReq request) {
        return Response.of(CalendarResultCode.CALENDAR_LIST_OK, calendarService.getCalendar(userId, request));
    }
}
