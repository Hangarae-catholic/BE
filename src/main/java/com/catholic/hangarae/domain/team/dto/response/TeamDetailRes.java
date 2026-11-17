package com.catholic.hangarae.domain.team.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.team.vo.TeamCategory;
import com.catholic.hangarae.domain.team.vo.TeamStatus;
import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TeamDetailRes(
        Long teamId,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Integer maxMember,
        Integer remainingMember,
        Long userId,
        String userName,
        StudentStatus studentStatus,
        DepartmentType department,
        UserCharacter character,
        TeamCategory category,
        TeamStatus teamStatus,
        String detail,
        List<TeamContactRes> contacts,
        List<String> imageUrls
) {
}
