package com.catholic.hangarae.domain.user.dto.response;

import com.catholic.hangarae.domain.interest.vo.InterestType;
import lombok.Builder;

@Builder
public record InterestRes(
        InterestType name
) {
}
