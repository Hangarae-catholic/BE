package com.catholic.hangarae.domain.user.repository;

import com.catholic.hangarae.domain.user.entity.EmailVerification;
import com.catholic.hangarae.domain.user.vo.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmailAndType(String email, VerificationType type);

    @Modifying
    @Query("delete from EmailVerification e where e.expiresAt <= :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
