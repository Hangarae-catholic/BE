package com.catholic.hangarae.domain.interest.entity;

import com.catholic.hangarae.domain.interest.vo.InterestType;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "interests")
@NoArgsConstructor
public class Interest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestType name;

    @Builder
    public Interest(InterestType name) {
        this.name = name;
    }
}
