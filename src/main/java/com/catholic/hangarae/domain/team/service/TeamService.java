package com.catholic.hangarae.domain.team.service;

import com.catholic.hangarae.domain.team.dto.request.TeamRequest;
import com.catholic.hangarae.domain.team.dto.response.TeamResponse;
import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.global.apiPayLoad.response.Response;

import java.util.List;

public interface TeamService {

    Long createTeam(Long userId, TeamRequest.CreateReq req);
    TeamResponse.DetailRes getTeamDetail(Long teamId);
    List<TeamResponse.ListRes> getTeamList();
    List<TeamResponse.ListRes> searchTeams(String keyword);

}
