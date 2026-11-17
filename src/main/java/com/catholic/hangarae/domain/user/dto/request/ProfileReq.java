package com.catholic.hangarae.domain.user.dto.request;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.interest.vo.InterestType;
import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProfileReq(
        @NotBlank String nickname,
        @NotNull UserCharacter character,
        @NotNull StudentStatus status,
        // 제1전공
        @NotNull DepartmentType primaryDepartment,
        // 복수전공
        DepartmentType doubleMajorDepartment,
        // 부전공
        DepartmentType minorDepartment,
        @NotNull @Size(min = 1, max = 4) List<@NotNull InterestType> interests
) {
}
