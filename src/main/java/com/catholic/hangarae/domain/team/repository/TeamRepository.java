package com.catholic.hangarae.domain.team.repository;

import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.domain.team.vo.TeamCategory;
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
    WHERE (:category IS NULL OR t.category = :category)
      AND (
          :keyword IS NULL
          OR t.title LIKE %:keyword%
          OR t.detail LIKE %:keyword%
      )
    ORDER BY
        CASE WHEN t.status = 'RECRUITING' THEN 0 ELSE 1 END,
        CASE WHEN :latestSort = true THEN t.createdAt END DESC,
        CASE WHEN :latestSort = false THEN t.createdAt END ASC,
        t.id DESC
""")
    List<Team> searchTeams(
            @Param("category") TeamCategory category,
            @Param("keyword") String keyword,
            @Param("latestSort") boolean latestSort
    );

    List<Team> findByEndAtBeforeAndStatus(LocalDateTime now, TeamStatus status);
    List<Team> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
