package com.catholic.hangarae.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueReq(
        @NotBlank
        String refreshToken
) {
}
