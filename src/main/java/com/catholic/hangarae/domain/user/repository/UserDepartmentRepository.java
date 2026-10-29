package com.catholic.hangarae.domain.user.repository;

import com.catholic.hangarae.domain.user.entity.UserDepartment;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {

    Optional<UserDepartment> findByUserIdAndType(
            Long userId,
            UserDepartmentType type
    );

    // 회원의 전공 목록 조회
    List<UserDepartment> findAllByUserIdOrderByIdAsc(Long userId);

    // 회원의 전공 선택 삭제
    void deleteAllByUserId(Long userId);
}
