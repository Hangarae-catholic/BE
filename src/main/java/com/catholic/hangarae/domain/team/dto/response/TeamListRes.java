package com.catholic.hangarae.domain.team.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.team.vo.TeamCategory;
import com.catholic.hangarae.domain.team.vo.TeamStatus;
import com.catholic.hangarae.domain.user.vo.StudentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TeamListRes(
        Long teamId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Integer maxMember,
        Integer remainingMember,
        String userName,
        StudentStatus studentStatus,
        DepartmentType department,
        TeamCategory category,
        TeamStatus teamStatus,
        String detail,
        String imageUrl,
        LocalDateTime createdAt
) {
}
