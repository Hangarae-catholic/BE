package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmailVerificationScheduler {

    private final EmailVerificationRepository emailVerificationRepository;

    // 만료된 인증번호 정리
    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteExpired() {
        emailVerificationRepository.deleteExpired(LocalDateTime.now());
    }
}
