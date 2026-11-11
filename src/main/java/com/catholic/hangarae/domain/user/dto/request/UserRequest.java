package com.catholic.hangarae.domain.user.dto.request;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.interest.vo.InterestType;
import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class UserRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProfileReq {
        @NotBlank
        private String nickname;
        @NotNull
        private UserCharacter character;
        @NotNull
        private StudentStatus status;
        // 제1전공
        @NotNull
        private DepartmentType primaryDepartment;
        // 복수전공
        private DepartmentType doubleMajorDepartment;
        // 부전공
        private DepartmentType minorDepartment;
        @NotNull
        @Size(min = 1, max = 4)
        private List<@NotNull InterestType> interests;
    }

}
