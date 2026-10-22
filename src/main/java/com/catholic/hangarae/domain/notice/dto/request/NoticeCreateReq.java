package com.catholic.hangarae.domain.notice.dto.request;

import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class NoticeCreateReq {

    @NotBlank
    private String name;

    @NotBlank
    private String responsible;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @NotNull
    private Location location;

    @NotBlank
    private String summary;

    @NotBlank
    private String information;

    @Size(max = 3)
    private List<String> imageUrls;

    @AssertTrue(message = "행사 시작일은 행사 종료일보다 빠르거나 같아야 합니다.")
    public boolean isValidPeriod() {
        if (startAt == null || endAt == null) {
            return true;
        }
        return !startAt.isAfter(endAt);
    }
}