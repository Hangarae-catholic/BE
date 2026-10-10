package com.catholic.hangarae.domain.team.entity;

import com.catholic.hangarae.domain.team.vo.UserTeamRole;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "user_teams",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_team_user_team", columnNames = {"user_id", "team_id"})
)
@NoArgsConstructor
public class UserTeam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserTeamRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Builder
    public UserTeam(UserTeamRole role, User user, Team team) {
        this.role = role;
        this.user = user;
        this.team = team;
    }
}
