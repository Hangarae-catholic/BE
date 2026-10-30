package com.catholic.hangarae.domain.user.dto.response;

import com.catholic.hangarae.domain.user.vo.KakaoNextStep;

public record KakaoLoginRes(
        KakaoNextStep nextStep,
        String email,
        TokenRes tokens
) {
}