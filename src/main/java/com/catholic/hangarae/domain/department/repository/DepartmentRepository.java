package com.catholic.hangarae.domain.department.repository;

import com.catholic.hangarae.domain.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // 학과 목록 조회
    List<Department> findAll();

    // 학과 단건 조회
    Optional<Department> findById(Long departmentId);
}
