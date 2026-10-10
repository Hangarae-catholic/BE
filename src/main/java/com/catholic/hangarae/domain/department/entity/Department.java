package com.catholic.hangarae.domain.department.entity;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "departments")
@NoArgsConstructor
public class Department extends BaseEntity {

    // 학과
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 계열
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Division division;

    // 학과명
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepartmentType name;

    @Builder
    public Department(Division division, DepartmentType name) {
        this.division = division;
        this.name = name;
    }
}
