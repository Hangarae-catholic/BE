package com.catholic.hangarae.domain.team.dto.response;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.team.vo.TeamCategory;
import com.catholic.hangarae.domain.team.vo.TeamContactType;
import com.catholic.hangarae.domain.team.vo.TeamStatus;
import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class TeamResponse{

    @Getter
    @Builder
    public static class DetailRes{
        private Long teamId;
        private String title;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private Integer maxMember;
        private Integer remainingMember;

        private Long userId;
        private String userName;
        private StudentStatus studentStatus;
        private DepartmentType department;
        private UserCharacter character;

        private TeamCategory category;
        private TeamStatus teamStatus;
        private String detail;

        private List<ContactRes> contacts;
        private List<String> imageUrls;
    }

    @Getter
    @Builder
    public static class ListRes{
        private Long teamId;
        private String title;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private Integer maxMember;
        private Integer remainingMember;

        private String userName;
        private StudentStatus studentStatus;
        private DepartmentType department;

        private TeamCategory category;
        private TeamStatus teamStatus;

        private String detail;
        private String imageUrl;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class ContactRes {
        private TeamContactType type;
        private String url;
    }
}
