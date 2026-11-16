package com.catholic.hangarae.domain.notice.dto.request;

import com.catholic.hangarae.domain.notice.vo.Location;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record NoticeCreateReq(
        @NotBlank String name,
        @NotBlank String responsible,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        @NotNull Location location,
        @Size(max = 255) String detailLocation,
        @NotBlank String information,
        @Size(max = 3) List<String> imageUrls
) {
    @AssertTrue(message = "행사 시작일은 행사 종료일보다 빠르거나 같아야 합니다.")
    public boolean isValidPeriod() {
        if (startAt == null || endAt == null) {
            return true;
        }
        return !startAt.isAfter(endAt);
    }
}
