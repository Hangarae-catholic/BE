package com.catholic.hangarae.domain.user.repository;


import com.catholic.hangarae.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    // 로그인 실패 횟수 동시 변경 방지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findUserByLoginId(String loginId);
    Optional<User> findById(Long userId);

    boolean existsByEmail(String email);
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    boolean existsByNicknameAndIdNot(String nickname, Long userId);

}
