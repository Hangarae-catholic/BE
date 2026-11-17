package com.catholic.hangarae.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignUpReq(
        @NotBlank String name,
        @NotBlank String loginId,
        @NotBlank String password,
        @NotBlank String confirmPassword
) {
}
