package com.catholic.hangarae.domain.notice.service;

import com.catholic.hangarae.domain.notice.dto.response.NoticeDetailRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeListRes;

import java.time.LocalDate;
import java.util.List;

public interface NoticeService {

    List<NoticeListRes> getNotices(LocalDate date);
    NoticeDetailRes getNotice(Long noticeId);
}
