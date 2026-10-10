package com.catholic.hangarae.domain.team.entity;

import com.catholic.hangarae.domain.team.vo.TeamCategory;
import com.catholic.hangarae.domain.team.vo.TeamStatus;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "teams")
@NoArgsConstructor
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private Integer maxMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamCategory category;

    @Column(nullable = false, columnDefinition = "text")
    private String detail;

    // 팀 연락 링크
    @Column(nullable = false, length = 2048)
    private String contactUrl;

    // 잔여 모집 인원
    @Column(nullable = false)
    private Integer remainingMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Team(String title, LocalDateTime startAt, LocalDateTime endAt, Integer maxMember, TeamCategory category, String detail, String contactUrl, User user) {
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.maxMember = maxMember;
        this.category = category;
        this.detail = detail;
        this.contactUrl = contactUrl;
        this.remainingMember = maxMember;
        this.status = TeamStatus.RECRUITING;
        this.user = user;
    }
}
