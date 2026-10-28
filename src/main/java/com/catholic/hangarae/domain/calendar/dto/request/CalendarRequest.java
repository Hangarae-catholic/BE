package com.catholic.hangarae.domain.calendar.dto.request;

import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class CalendarRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ListReq {
        // 조회 연도와 월
        @NotNull
        @Min(1)
        @Max(9999)
        private Integer year;
        @NotNull
        @Min(1)
        @Max(12)
        private Integer month;
        // 선택 계열, 1~2개
        @NotNull
        @Size(min = 1, max = 2)
        private List<@NotNull Division> divisions;
        // 일정 유형, 1~2개
        @NotNull
        @Size(min = 1, max = 2)
        private List<@NotNull NoticeCategory> categories;
    }
}
