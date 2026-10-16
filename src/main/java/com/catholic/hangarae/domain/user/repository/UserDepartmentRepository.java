package com.catholic.hangarae.domain.user.repository;

import com.catholic.hangarae.domain.user.entity.UserDepartment;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {

    Optional<UserDepartment> findByUserIdAndType(
            Long userId,
            UserDepartmentType type
    );
}