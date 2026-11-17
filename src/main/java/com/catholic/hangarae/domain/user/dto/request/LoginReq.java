package com.catholic.hangarae.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginReq(
        @NotBlank String loginId,
        @NotBlank String password
) {
}
