package com.catholic.hangarae.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCodeSendReq(
        @NotBlank
        @Email
        String email
) {
}
