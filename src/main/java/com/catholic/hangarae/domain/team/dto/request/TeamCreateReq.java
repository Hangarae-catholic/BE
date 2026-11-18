package com.catholic.hangarae.domain.team.dto.request;

import com.catholic.hangarae.domain.team.vo.TeamCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record TeamCreateReq(
        @NotBlank String title,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        @NotNull @Min(2) Integer maxMember,
        @NotNull TeamCategory category,
        @NotBlank String detail,
        @NotEmpty(message = "연락수단은 최소 1개 이상 등록해야 합니다.") @Size(max = 3) List<@Valid TeamContactReq> contacts,
        @Size(max = 3) List<@NotBlank String> imageUrls
) {
    @AssertTrue(message = "모집 시작일은 모집 종료일보다 빠르거나 같아야 합니다.")
    public boolean isValidPeriod() {
        if (startAt == null || endAt == null) {
            return true;
        }

        return !startAt.isAfter(endAt);
    }
}
