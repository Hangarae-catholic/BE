package com.catholic.hangarae.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetSendReq(
        @NotBlank
        String loginId,

        @NotBlank
        @Email
        String email
) {
}
