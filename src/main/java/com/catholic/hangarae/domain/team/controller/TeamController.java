package com.catholic.hangarae.domain.team.controller;

import com.catholic.hangarae.domain.team.dto.request.TeamRequest;
import com.catholic.hangarae.domain.team.dto.response.TeamResponse;
import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.domain.team.service.TeamService;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
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
@RequestMapping("/api/team")
@AllArgsConstructor
@Tag(name="Team", description = "팀 매칭 API")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "팀 매칭 게시글 생성", description = "팀 매칭 게시글을 생성합니다.")
    public ResponseEntity<Response<Long>> createTeam(@AuthUser Long userId, @Valid @RequestBody TeamRequest.CreateReq req) {
        Long teamId = teamService.createTeam(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.ok(teamId));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "팀 매칭 상세조회", description = "특정 팀 매칭 게시글을 상세조회 합니다.")
    public ResponseEntity<Response<TeamResponse.DetailRes>> getTeamDetail(@PathVariable Long teamId) {
        TeamResponse.DetailRes response = teamService.getTeamDetail(teamId);
        return ResponseEntity.status(HttpStatus.OK).body(Response.ok(response));
    }

    @GetMapping
    @Operation(summary = "팀 매칭 목록 조회", description = "팀 매칭 게시글 목록을 조회합니다.")
    public ResponseEntity<Response<List<TeamResponse.ListRes>>> getTeamList() {
        List<TeamResponse.ListRes> response = teamService.getTeamList();
        return ResponseEntity.status(HttpStatus.OK).body(Response.ok(response));
    }

    @GetMapping("/search")
    @Operation(summary = "팀 매칭 검색", description = "팀 매칭 게시글을 검색합니다.")
    public ResponseEntity<Response<List<TeamResponse.ListRes>>> searchTeams(
            @RequestParam String keyword) {
        List<TeamResponse.ListRes> response = teamService.searchTeams(keyword);
        return ResponseEntity.ok(Response.ok(response));
    }
    
}
