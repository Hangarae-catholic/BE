package com.catholic.hangarae.domain.user.dto.response;

public record TokenRes(
        String accessToken,
        String refreshToken
) {
}
