package com.catholic.hangarae.domain.notice.dto.response;

import com.catholic.hangarae.domain.notice.vo.Location;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeListRes {

    private Long noticeId;
    private String name;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Location location;
    private String summary;
    private boolean approved;

}
