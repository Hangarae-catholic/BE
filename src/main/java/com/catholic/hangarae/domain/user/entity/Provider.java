package com.catholic.hangarae.domain.user.entity;

import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "provider")
@NoArgsConstructor
public class Provider extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String providerId;

    private String provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Provider(String providerId, String provider, User user) {
        this.providerId = providerId;
        this.provider = provider;
        this.user = user;
    }
}
