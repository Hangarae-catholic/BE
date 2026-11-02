package com.catholic.hangarae.domain.team.repository;

import com.catholic.hangarae.domain.team.entity.TeamContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamContactRepository extends JpaRepository<TeamContact, Long> {

    List<TeamContact> findAllByTeamId(Long teamId);
    void deleteAllByTeamId(Long teamId);

}