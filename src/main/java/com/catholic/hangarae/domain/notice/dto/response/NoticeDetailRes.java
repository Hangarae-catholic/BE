package com.catholic.hangarae.domain.notice.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import com.catholic.hangarae.domain.notice.vo.NoticeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NoticeDetailRes {

    private Long noticeId;
    private String name;
    private String summary;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Location location;
    private String responsible;
    private String information;
    private String url;
    private NoticeType type;
    private NoticeCategory category;
    private boolean approved;
    private Division division;
    private DepartmentType department;
    private List<String> imageUrls;

}
