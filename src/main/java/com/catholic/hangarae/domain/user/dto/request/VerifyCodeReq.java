package com.catholic.hangarae.domain.user.dto.request;

import com.catholic.hangarae.domain.user.vo.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyCodeReq(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String code,

        @NotNull
        VerificationType type
) {
}
