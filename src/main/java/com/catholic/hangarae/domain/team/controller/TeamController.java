package com.catholic.hangarae.domain.team.controller;

import com.catholic.hangarae.domain.team.dto.request.TeamRequest;
import com.catholic.hangarae.domain.team.dto.response.TeamResponse;
import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.domain.team.service.TeamService;
import com.catholic.hangarae.domain.team.vo.TeamCategory;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.TeamResultCode;
import com.catholic.hangarae.global.security.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@AllArgsConstructor
@Tag(name="Team", description = "팀 매칭 API")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "팀 매칭 게시글 생성")
    public Response<Long> createTeam(@AuthUser Long userId, @Valid @RequestBody TeamRequest.CreateReq req) {
        Long teamId = teamService.createTeam(userId, req);
        return Response.of(TeamResultCode.TEAM_CREATE_OK, teamId);}

    @GetMapping
    @Operation(summary = "팀 매칭 목록 조회 및 검색", description = "팀 매칭 목록을 전체 조회, 검색, 카테고리별 조회, 등록일순 정렬.")
    public Response<List<TeamResponse.ListRes>> getTeams(
            @RequestParam(required = false) TeamCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "true") boolean latestFirst
    ) {
        List<TeamResponse.ListRes> response = teamService.getTeams(category, keyword, latestFirst);
        return Response.of(TeamResultCode.TEAM_LIST_OK, response);
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 매칭 상세조회", description = "특정 팀 매칭 게시글을 상세조회 합니다.")
    public Response<TeamResponse.DetailRes> getTeamDetail(@PathVariable Long teamId) {
        TeamResponse.DetailRes response = teamService.getTeamDetail(teamId);
        return Response.of(TeamResultCode.TEAM_DETAIL_OK, response);
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "팀 매칭 게시글 삭제")
    public Response<Void> deleteTeam(@AuthUser Long userId, @PathVariable Long teamId) {
        return Response.of(TeamResultCode.TEAM_DELETE_OK, teamService.deleteTeam(userId, teamId));
    }

    @PutMapping("/{teamId}")
    @Operation(summary = "팀 매칭 게시글 수정")
    public Response<Long> updateTeam(@AuthUser Long userId, @PathVariable Long teamId,
                                                     @Valid @RequestBody TeamRequest.UpdateReq req) {
        return Response.of(TeamResultCode.TEAM_UPDATE_OK, teamService.updateTeam(userId, teamId, req));
    }

    @GetMapping("/my")
    @Operation(summary = "사용자가 작성한 팀 매칭 목록 조회")
    public Response<List<TeamResponse.ListRes>> getMyTeams(@AuthUser Long userId) {
        List<TeamResponse.ListRes> response = teamService.getMyTeams(userId);
        return Response.of(TeamResultCode.TEAM_MY_LIST_OK, response);
    }

    @PatchMapping("/{teamId}/close")
    @Operation(summary = "팀 매칭 수동 마감")
    public Response<Long> closeTeam(@AuthUser Long userId, @PathVariable Long teamId) {
        return Response.of(TeamResultCode.TEAM_CLOSE_SUCCESS, teamService.closeTeam(userId, teamId));
    }
}
