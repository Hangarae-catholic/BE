package com.catholic.hangarae.domain.team.service;

import com.catholic.hangarae.domain.team.dto.request.TeamCreateReq;
import com.catholic.hangarae.domain.team.dto.request.TeamUpdateReq;
import com.catholic.hangarae.domain.team.dto.response.TeamDetailRes;
import com.catholic.hangarae.domain.team.dto.response.TeamListRes;
import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.domain.team.vo.TeamCategory;
import com.catholic.hangarae.global.apiPayLoad.response.Response;

import java.util.List;

public interface TeamService {

    Long createTeam(Long userId, TeamCreateReq req);
    TeamDetailRes getTeamDetail(Long teamId);
    List<TeamListRes> getTeams(TeamCategory category, String keyword,boolean latestFirst);
    Void deleteTeam(Long userId, Long teamId);
    Long updateTeam(Long userId, Long teamId, TeamUpdateReq req);
    void closeExpiredTeams();
    List<TeamListRes> getMyTeams(Long userId);
    Long closeTeam(Long userId, Long teamId);

}
