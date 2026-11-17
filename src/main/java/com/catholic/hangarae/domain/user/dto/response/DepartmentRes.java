package com.catholic.hangarae.domain.user.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import lombok.Builder;

@Builder
public record DepartmentRes(
        DepartmentType name,
        Division division,
        UserDepartmentType type
) {
}
