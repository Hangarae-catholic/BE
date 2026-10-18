package com.catholic.hangarae.domain.notice.controller;

import com.catholic.hangarae.domain.notice.dto.response.NoticeDetailRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeListRes;
import com.catholic.hangarae.domain.notice.service.NoticeService;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.NoticeResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "Notice", description = "행사 조회 API")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @Operation(summary = "행사 목록 조회", description = "날짜 생략 시 오늘, 오늘부터 7일 이내의 행사 목록 조회")
    public Response<List<NoticeListRes>> getNotices(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Response.of(NoticeResultCode.NOTICE_LIST_OK, noticeService.getNotices(date));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "행사 상세 조회")
    public Response<NoticeDetailRes> getNotice(@PathVariable Long noticeId) {
        return Response.of(NoticeResultCode.NOTICE_DETAIL_OK, noticeService.getNotice(noticeId));
    }
}
