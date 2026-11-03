package com.catholic.hangarae.domain.user.dto.response;

import com.catholic.hangarae.domain.user.vo.OAuthNextStep;

public record OAuthLoginRes(
        OAuthNextStep nextStep,
        String email,
        TokenRes tokens
) {
}