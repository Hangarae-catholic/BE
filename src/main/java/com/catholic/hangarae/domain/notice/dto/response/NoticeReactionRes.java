package com.catholic.hangarae.domain.notice.dto.response;

import com.catholic.hangarae.domain.notice.vo.NoticeReactionType;
import lombok.Builder;

@Builder
public record NoticeReactionRes(
        Long likeCount,
        Long dislikeCount,
        NoticeReactionType userReaction // 로그인한 사용자가 누른 반응
) {
}
