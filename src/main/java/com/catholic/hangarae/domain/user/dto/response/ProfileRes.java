package com.catholic.hangarae.domain.user.dto.response;

import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileRes(
        Long userId,
        String name,
        String nickname,
        UserCharacter character,
        StudentStatus status,
        List<DepartmentRes> departments,
        List<InterestRes> interests
) {
}
