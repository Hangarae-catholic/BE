package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.entity.EmailVerification;
import com.catholic.hangarae.domain.user.repository.EmailVerificationRepository;
import com.catholic.hangarae.domain.user.vo.VerificationType;
import com.catholic.hangarae.global.apiPayLoad.error.MailErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int RESEND_INTERVAL_SECONDS = 60;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationRepository emailVerificationRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Override
    @Transactional
    public void sendCode(String email, VerificationType type) {
        LocalDateTime now = LocalDateTime.now();
        String code = String.valueOf(100000 + RANDOM.nextInt(900000));

        Optional<EmailVerification> existing = emailVerificationRepository.findByEmailAndType(email, type);
        if (existing.isPresent()) {
            // 메일 폭탄 방지, 직전 인증번호가 유효하므로 오류 없이 넘어가 계정 존재 여부도 노출하지 않음
            if (existing.get().getSentAt().plusSeconds(RESEND_INTERVAL_SECONDS).isAfter(now)) {
                return;
            }
            existing.get().renew(code, now);
        } else {
            EmailVerification verification = EmailVerification.builder()
                    .email(email)
                    .type(type)
                    .code(code)
                    .now(now)
                    .build();
            emailVerificationRepository.save(verification);
        }

        // 메일 전송 실패 시 저장한 인증번호도 함께 롤백
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("[행가래] 이메일 인증번호");
            message.setText("인증번호는 " + code + " 입니다.\n5분 안에 입력해 주세요.\n본인이 요청하지 않았다면 이 메일을 무시해 주세요.");
            mailSender.send(message);
        } catch (MailException e) {
            log.error("인증 메일 전송 실패", e);
            throw new BusinessException(MailErrorCode.EMAIL_SEND_FAILED);
        }
    }

    // 실패 횟수와 만료 삭제는 예외가 나도 저장
    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public void verifyCode(String email, String code, VerificationType type) {
        EmailVerification verification = emailVerificationRepository.findByEmailAndType(email, type)
                .orElseThrow(() -> new BusinessException(MailErrorCode.INVALID_VERIFICATION_CODE));

        LocalDateTime now = LocalDateTime.now();
        if (verification.isExpired(now)) {
            emailVerificationRepository.delete(verification);
            throw new BusinessException(MailErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (!verification.getCode().equals(code)) {
            verification.recordFailure();
            if (verification.isAttemptsExceeded()) {
                emailVerificationRepository.delete(verification);
                throw new BusinessException(MailErrorCode.VERIFICATION_ATTEMPTS_EXCEEDED);
            }
            throw new BusinessException(MailErrorCode.INVALID_VERIFICATION_CODE);
        }

        verification.verify(now);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkVerified(String email, VerificationType type) {
        getVerified(email, type);
    }

    // 인증을 마친 본인만 다음 단계를 진행하도록 인증번호까지 함께 확인
    @Override
    @Transactional(readOnly = true)
    public void checkVerified(String email, String code, VerificationType type) {
        if (!getVerified(email, type).getCode().equals(code)) {
            throw new BusinessException(MailErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    private EmailVerification getVerified(String email, VerificationType type) {
        EmailVerification verification = emailVerificationRepository.findByEmailAndType(email, type)
                .orElseThrow(() -> new BusinessException(MailErrorCode.EMAIL_NOT_VERIFIED));

        if (verification.isExpired(LocalDateTime.now())) {
            throw new BusinessException(MailErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        if (!verification.isVerified()) {
            throw new BusinessException(MailErrorCode.EMAIL_NOT_VERIFIED);
        }
        return verification;
    }

    @Override
    @Transactional
    public void delete(String email, VerificationType type) {
        emailVerificationRepository.findByEmailAndType(email, type)
                .ifPresent(emailVerificationRepository::delete);
    }
}
