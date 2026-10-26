package com.catholic.hangarae.domain.user.repository;

import com.catholic.hangarae.domain.user.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    // 회원의 관심 분야 목록
    List<UserInterest> findAllByUserIdOrderByIdAsc(Long userId);

    // 회원의 관심 분야 선택 삭제
    void deleteAllByUserId(Long userId);
}
