package com.catholic.hangarae.domain.notice.dto.request;

import com.catholic.hangarae.domain.notice.vo.NoticeReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeReactionReq {

    @NotNull
    private NoticeReactionType type;
}
