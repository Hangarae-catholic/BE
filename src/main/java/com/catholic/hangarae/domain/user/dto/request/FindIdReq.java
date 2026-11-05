package com.catholic.hangarae.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindIdReq(
        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String code
) {
}
