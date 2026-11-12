package com.catholic.hangarae.domain.notice.controller;

import com.catholic.hangarae.domain.notice.dto.request.NoticeCreateReq;
import com.catholic.hangarae.domain.notice.dto.request.NoticeReactionReq;
import com.catholic.hangarae.domain.notice.dto.response.NoticeDetailRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeListRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeReactionRes;
import com.catholic.hangarae.domain.notice.service.NoticeService;
import com.catholic.hangarae.domain.team.dto.response.TeamResponse;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.NoticeResultCode;
import com.catholic.hangarae.global.apiPayLoad.result.TeamResultCode;
import com.catholic.hangarae.global.security.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
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

    @GetMapping("/recommend")
    @Operation(summary = "오늘의 추천 행사", description = "오늘 열리는 행사 중 관심 분야와 학과에 맞는 행사를 관심 분야 점수 순으로 조회")
    public Response<List<NoticeListRes>> getRecommendedNotices(
            @AuthUser Long userId, @RequestParam(defaultValue = "5") int size) {
        return Response.of(NoticeResultCode.NOTICE_RECOMMEND_OK, noticeService.getRecommendedNotices(userId, size));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "행사 상세 조회")
    public Response<NoticeDetailRes> getNotice(@PathVariable Long noticeId) {
        return Response.of(NoticeResultCode.NOTICE_DETAIL_OK, noticeService.getNotice(noticeId));
    }

    @GetMapping("/{noticeId}/calendar-file")
    @Operation(summary = "캘린더에 추가할 일정 파일", description = "기기 기본 캘린더에 추가할 수 있는 iCalendar(.ics) 파일을 내려받습니다.")
    public ResponseEntity<byte[]> getCalendarFile(@PathVariable Long noticeId) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"hangarae-" + noticeId + ".ics\"")
                .body(noticeService.getCalendarFile(noticeId).getBytes(StandardCharsets.UTF_8));
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

    @GetMapping("/my")
    @Operation(summary = "사용자가 작성한 행사 목록 조회")
    public Response<List<NoticeListRes>> getMyNotices(@AuthUser Long userId) {
        List<NoticeListRes> response = noticeService.getMyNotices(userId);
        return Response.of(NoticeResultCode.NOTICE_MY_LIST_OK, response);
    }
}
