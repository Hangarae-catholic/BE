package com.catholic.hangarae.domain.user.repository;

import com.catholic.hangarae.domain.user.entity.UserDepartment;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {

    // 회원의 전공 목록
    List<UserDepartment> findAllByUserIdOrderByIdAsc(Long userId);

    // 회원의 전공 선택 삭제
    void deleteAllByUserId(Long userId);
}
