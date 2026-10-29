package com.catholic.hangarae.domain.notice.entity;

import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.department.entity.Department;
import com.catholic.hangarae.domain.notice.vo.Location;
import com.catholic.hangarae.domain.notice.vo.NoticeType;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notices")
@NoArgsConstructor
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    // 행사 한줄 소개
    private String summary;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Column(columnDefinition = "text")
    private String information;

    @Enumerated(EnumType.STRING)
    private Location location;

    private String responsible;

    @Column(length = 2048)
    private String url;

    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @Enumerated(EnumType.STRING)
    private NoticeCategory category;

    // 관리자 승인 여부
    @Column(nullable = false)
    private boolean approved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // 학생 등록 행사인 경우 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notice(String name, String summary, LocalDateTime startAt, LocalDateTime endAt, String information, Location location, String responsible, String url, NoticeType type, NoticeCategory category, Department department, User user) {
        this.name = name;
        this.summary = summary;
        this.startAt = startAt;
        this.endAt = endAt;
        this.information = information;
        this.location = location;
        this.responsible = responsible;
        this.url = url;
        this.type = type;
        this.category = category;
        this.department = department;
        this.user = user;
        this.approved = false;
    }
}
