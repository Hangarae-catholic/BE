package com.catholic.hangarae.domain.team.dto.request;

import com.catholic.hangarae.domain.team.vo.TeamCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class TeamRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateReq {

        @NotBlank
        private String title;

        @NotNull
        private LocalDateTime startAt;

        @NotNull
        private LocalDateTime endAt;

        @NotNull
        @Min(2)
        private Integer maxMember;

        @NotNull
        private TeamCategory category;

        @NotBlank
        private String detail;

        @NotBlank
        private String contactUrl;

        @Size(max = 3)
        private List<@NotBlank String> imageUrls;

        @AssertTrue(message = "모집 시작일은 모집 종료일보다 빠르거나 같아야 합니다.")
        public boolean isValidPeriod() {
            if (startAt == null || endAt == null) {
                return true;
            }

            return !startAt.isAfter(endAt);
        }

        @AssertTrue(message = "모집 시작일은 현재 시각 이후여야 합니다.")
        public boolean isStartAtValid() {
            if (startAt == null) return true;
            return !startAt.isBefore(LocalDateTime.now());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateReq {

        @NotBlank
        private String title;

        @NotNull
        private LocalDateTime startAt;

        @NotNull
        private LocalDateTime endAt;

        @NotNull
        @Min(0)
        private Integer remainingMember; // 남은 모집 인원

        @NotNull
        private TeamCategory category;

        @NotBlank
        private String detail;

        @NotBlank
        private String contactUrl;

        @Size(max = 3)
        private List<@NotBlank String> imageUrls;

        @AssertTrue(message = "모집 시작일은 모집 종료일보다 빠르거나 같아야 합니다.")
        public boolean isValidPeriod() {
            if (startAt == null || endAt == null) return true;
            return !startAt.isAfter(endAt);
        }
    }
}
