package com.catholic.hangarae.domain.notice.dto.response;

import com.catholic.hangarae.domain.notice.vo.Location;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NoticeListRes(
        Long noticeId,
        String name,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Location location,
        String detailLocation,
        boolean approved,
        List<String> imageUrls
) {
}
