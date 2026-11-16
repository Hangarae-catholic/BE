package com.catholic.hangarae.domain.team.dto.response;

import com.catholic.hangarae.domain.team.vo.TeamContactType;
import lombok.Builder;

@Builder
public record TeamContactRes(
        TeamContactType type,
        String url
) {
}
