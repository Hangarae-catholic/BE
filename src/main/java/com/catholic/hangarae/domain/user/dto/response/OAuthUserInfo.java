package com.catholic.hangarae.domain.user.dto.response;

public record OAuthUserInfo(
        String providerId,
        String email
) {
}