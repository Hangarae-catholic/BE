package com.catholic.hangarae.domain.user.entity;

import com.catholic.hangarae.domain.department.entity.Department;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "user_departments",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_department_user_department", columnNames = {"user_id", "department_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDepartment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserDepartmentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Builder
    public UserDepartment(UserDepartmentType type, User user, Department department) {
        this.type = type;
        this.user = user;
        this.department = department;
    }
}
