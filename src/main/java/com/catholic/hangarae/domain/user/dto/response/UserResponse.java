package com.catholic.hangarae.domain.user.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.interest.vo.InterestType;
import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class UserResponse {

    @Getter
    @Builder
    public static class ProfileRes {
        private Long userId;
        private String name;
        private String nickname;
        private UserCharacter character;
        private StudentStatus status;
        private List<DepartmentRes> departments;
        private List<InterestRes> interests;
    }

    @Getter
    @Builder
    public static class DepartmentRes {
        private Long departmentId;
        private DepartmentType name;
        private Division division;
        private UserDepartmentType type;
    }

    @Getter
    @Builder
    public static class InterestRes {
        private Long interestId;
        private InterestType name;
    }

    @Getter
    @Builder
    public static class ProfileOptionsRes {
        private List<UserCharacter> characters;
        private List<StudentStatus> statuses;
        private List<DepartmentRes> departments;
        private List<InterestRes> interests;
    }
}
