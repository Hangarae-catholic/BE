package com.catholic.hangarae.domain.team.repository;

import com.catholic.hangarae.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTitleContainingOrDetailContaining(
            String titleKeyword,
            String detailKeyword
    );
}
