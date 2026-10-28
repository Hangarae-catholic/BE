package com.catholic.hangarae.domain.notice.dto.response;

import com.catholic.hangarae.domain.notice.vo.NoticeReactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeReactionRes {

    private Long likeCount;
    private Long dislikeCount;
    private NoticeReactionType userReaction; // 로그인한 사용자가 누른 반응
}