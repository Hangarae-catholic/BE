package com.catholic.hangarae.domain.interest.repository;

import com.catholic.hangarae.domain.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    // 관심 분야 목록 조회
    List<Interest> findAll();

    // 관심 분야 단건 조회
    Optional<Interest> findById(Long interestId);
}
