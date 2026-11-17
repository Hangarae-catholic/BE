package com.catholic.hangarae.domain.notice.dto.request;

import com.catholic.hangarae.domain.notice.vo.NoticeReactionType;
import jakarta.validation.constraints.NotNull;

public record NoticeReactionReq(
        @NotNull NoticeReactionType type
) {
}
