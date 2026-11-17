package com.catholic.hangarae.domain.calendar.service;

import com.catholic.hangarae.domain.calendar.dto.request.CalendarAcademicReq;
import com.catholic.hangarae.domain.calendar.dto.request.CalendarListReq;
import com.catholic.hangarae.domain.calendar.dto.response.CalendarAcademicRes;
import com.catholic.hangarae.domain.calendar.dto.response.CalendarNoticeRes;

import java.util.List;

public interface CalendarService {

    List<CalendarNoticeRes> getCalendar(Long userId, CalendarListReq request);

    List<CalendarAcademicRes> getAcademicCalendar(CalendarAcademicReq request);
}
