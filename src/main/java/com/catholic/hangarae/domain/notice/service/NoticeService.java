package com.catholic.hangarae.domain.notice.service;

import com.catholic.hangarae.domain.notice.dto.request.NoticeCreateReq;
import com.catholic.hangarae.domain.notice.dto.request.NoticeReactionReq;
import com.catholic.hangarae.domain.notice.dto.response.NoticeDetailRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeListRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeReactionRes;

import java.time.LocalDate;
import java.util.List;

public interface NoticeService {

    List<NoticeListRes> getNotices(LocalDate date);
    NoticeDetailRes getNotice(Long noticeId);

    Long createNotice(Long userId, NoticeCreateReq req);
    Void deleteNotice(Long userId, Long NoticeId);

    Void reactToNotice(Long userId, Long noticeId, NoticeReactionReq req);
    NoticeReactionRes getNoticeReaction(Long userId, Long noticeId);
}
