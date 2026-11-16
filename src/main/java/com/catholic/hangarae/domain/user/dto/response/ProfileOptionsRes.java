package com.catholic.hangarae.domain.user.dto.response;

import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileOptionsRes(
        List<UserCharacter> characters,
        List<StudentStatus> statuses,
        List<DepartmentRes> departments,
        List<InterestRes> interests
) {
}
