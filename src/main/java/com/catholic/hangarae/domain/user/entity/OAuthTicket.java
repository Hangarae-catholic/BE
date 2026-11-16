
package com.catholic.hangarae.domain.user.entity;

import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "oauth_ticket")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthTicket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ticket;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    public OAuthTicket(String ticket, String payload, LocalDateTime expiresAt) {
        this.ticket = ticket;
        this.payload = payload;
        this.expiresAt = expiresAt;
    }
}
