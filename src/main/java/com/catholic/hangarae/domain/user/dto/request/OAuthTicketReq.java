
package com.catholic.hangarae.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthTicketReq(
        @NotBlank
        String ticket
) {
}
