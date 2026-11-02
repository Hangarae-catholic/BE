package com.catholic.hangarae.domain.team.repository;

import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.domain.team.vo.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {


    @Query("""
    SELECT t FROM Team t
    ORDER BY
        CASE WHEN t.status = 'RECRUITING' THEN 0 ELSE 1 END,
        t.id DESC
""")
    List<Team> findAllOrderByRecruitingFirst();


    @Query("""
    SELECT t FROM Team t
    WHERE t.title LIKE %:keyword%
       OR t.detail LIKE %:keyword%
    ORDER BY
        CASE WHEN t.status = 'RECRUITING' THEN 0 ELSE 1 END,
        t.id DESC
""")
    List<Team> searchOrderByRecruitingFirst(
            @Param("keyword") String keyword
    );


    List<Team> findByEndAtBeforeAndStatus(LocalDateTime now, TeamStatus status);
    List<Team> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
