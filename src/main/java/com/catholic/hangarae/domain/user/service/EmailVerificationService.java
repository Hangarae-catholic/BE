package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.vo.VerificationType;

public interface EmailVerificationService {
    void sendCode(String email, VerificationType type);
    void verifyCode(String email, String code, VerificationType type);
    void checkVerified(String email, String code, VerificationType type);
    void delete(String email, VerificationType type);
}
