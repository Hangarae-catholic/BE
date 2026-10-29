package com.catholic.hangarae.domain.calendar.service;

import com.catholic.hangarae.domain.calendar.dto.request.CalendarRequest;
import com.catholic.hangarae.domain.calendar.dto.response.CalendarResponse;

import java.util.List;

public interface CalendarService {

    List<CalendarResponse.NoticeRes> getCalendar(Long userId, CalendarRequest.ListReq request);
}
