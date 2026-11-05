package com.catholic.hangarae.domain.user.entity;

import com.catholic.hangarae.domain.user.vo.VerificationType;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "email_verifications", uniqueConstraints = @UniqueConstraint(columnNames = {"email", "type"}))
@NoArgsConstructor
public class EmailVerification extends BaseEntity {

    private static final int VALID_MINUTES = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationType type;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean verified;

    private int failedAttempts;

    @Builder
    public EmailVerification(String email, VerificationType type, String code, LocalDateTime now) {
        this.email = email;
        this.type = type;
        renew(code, now);
    }

    // 새 인증번호 발급, 이전 인증 상태와 실패 횟수 초기화
    public void renew(String code, LocalDateTime now) {
        this.code = code;
        this.sentAt = now;
        this.expiresAt = now.plusMinutes(VALID_MINUTES);
        this.verified = false;
        this.failedAttempts = 0;
    }

    public boolean isExpired(LocalDateTime now) {
        return !expiresAt.isAfter(now);
    }

    // 인증 성공 시점부터 다음 단계를 마칠 시간을 다시 부여
    public void verify(LocalDateTime now) {
        if (!verified) {
            this.verified = true;
            this.expiresAt = now.plusMinutes(VALID_MINUTES);
        }
    }

    public void recordFailure() {
        this.failedAttempts++;
    }

    public boolean isAttemptsExceeded() {
        return failedAttempts >= MAX_FAILED_ATTEMPTS;
    }
}
