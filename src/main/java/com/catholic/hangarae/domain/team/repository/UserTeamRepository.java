package com.catholic.hangarae.domain.team.repository;

import com.catholic.hangarae.domain.team.entity.TeamImage;
import com.catholic.hangarae.domain.team.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {

    void deleteAllByTeamId(Long teamId);

}
