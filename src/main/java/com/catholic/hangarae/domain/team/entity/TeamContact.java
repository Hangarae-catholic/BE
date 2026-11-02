package com.catholic.hangarae.domain.team.entity;

import com.catholic.hangarae.domain.team.vo.TeamContactType;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "team_contacts")
@NoArgsConstructor
public class TeamContact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamContactType type;

    @Column(nullable = false, length = 2048)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Builder
    public TeamContact(TeamContactType type, String url, Team team) {
        this.type = type;
        this.url = url;
        this.team = team;
    }
}