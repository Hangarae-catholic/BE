package com.catholic.hangarae.domain.team.dto.request;

import com.catholic.hangarae.domain.team.vo.TeamContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamContactReq(
        @NotNull TeamContactType type,
        @NotBlank String url
) {
}
