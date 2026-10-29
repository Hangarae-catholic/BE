package com.catholic.hangarae.domain.notice.controller;

import com.catholic.hangarae.domain.notice.dto.request.NoticeCreateReq;
import com.catholic.hangarae.domain.notice.dto.request.NoticeReactionReq;
import com.catholic.hangarae.domain.notice.dto.response.NoticeDetailRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeListRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeReactionRes;
import com.catholic.hangarae.domain.notice.service.NoticeService;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.NoticeResultCode;
import com.catholic.hangarae.global.security.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @Operation(summary = "행사 등록", description = "학생이 사설 행사 등록")
    public Response<Long> createNotice(@AuthUser Long userId, @Valid @RequestBody NoticeCreateReq req) {
        return Response.of(NoticeResultCode.NOTICE_CREATE_OK, noticeService.createNotice(userId, req));
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "행사 삭제", description = "사설 행사 삭제")
    public Response<Void> deleteNotice(@AuthUser Long userId, @PathVariable Long noticeId) {
        return Response.of(NoticeResultCode.NOTICE_DELETE_OK, noticeService.deleteNotice(userId, noticeId));
    }

    @PostMapping("/{noticeId}/reaction")
    @Operation(summary = "행사 추천/비추천", description = "사설 행사 추천/비추천 기능")
    public Response<Void> reactToNotice(@AuthUser Long userId, @PathVariable Long noticeId, @Valid @RequestBody NoticeReactionReq req) {
        return Response.of(NoticeResultCode.NOTICE_REACT_OK, noticeService.reactToNotice(userId, noticeId, req));
    }

    @GetMapping("{noticeId}/reaction")
    @Operation(summary = "행사 추천/비추천 조회", description = "사설 행사 추천/비추천 조회")
    public Response<NoticeReactionRes> getNoticeReaction(@AuthUser Long userId, @PathVariable Long noticeId) {
        return Response.of(NoticeResultCode.NOTICE_REACT_GET_OK, noticeService.getNoticeReaction(userId, noticeId));
    }
}
