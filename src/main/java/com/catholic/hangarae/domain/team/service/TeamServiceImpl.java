package com.catholic.hangarae.domain.team.service;


import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.image.entity.Image;
import com.catholic.hangarae.domain.image.repository.ImageRepository;
import com.catholic.hangarae.domain.team.dto.request.TeamRequest;
import com.catholic.hangarae.domain.team.dto.response.TeamResponse;
import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.domain.team.entity.TeamContact;
import com.catholic.hangarae.domain.team.entity.TeamImage;
import com.catholic.hangarae.domain.team.entity.UserTeam;
import com.catholic.hangarae.domain.team.repository.TeamContactRepository;
import com.catholic.hangarae.domain.team.repository.TeamImageRepository;
import com.catholic.hangarae.domain.team.repository.TeamRepository;
import com.catholic.hangarae.domain.team.repository.UserTeamRepository;
import com.catholic.hangarae.domain.team.vo.TeamCategory;
import com.catholic.hangarae.domain.team.vo.TeamStatus;
import com.catholic.hangarae.domain.team.vo.UserTeamRole;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.user.entity.UserDepartment;
import com.catholic.hangarae.domain.user.repository.UserDepartmentRepository;
import com.catholic.hangarae.domain.user.repository.UserRepository;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import com.catholic.hangarae.global.apiPayLoad.error.TeamErrorCode;
import com.catholic.hangarae.global.apiPayLoad.error.UserErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final TeamImageRepository teamImageRepository;
    private final UserDepartmentRepository userDepartmentRepository;
    private final UserTeamRepository userTeamRepository;
    private final TeamContactRepository teamContactRepository;

    // 팀 생성
    @Transactional
    @Override
    public Long createTeam(Long userId, TeamRequest.CreateReq req) {

        User user=userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Team team = Team.builder()
                .title(req.getTitle())
                .startAt(req.getStartAt())
                .endAt(req.getEndAt())
                .maxMember(req.getMaxMember())
                .category(req.getCategory())
                .detail(req.getDetail())
                .user(user)
                .build();
        Team savedTeam = teamRepository.save(team);

        UserTeam userTeam = UserTeam.builder()
                .user(user)
                .team(team)
                .role(UserTeamRole.LEADER)
                .build();
        userTeamRepository.save(userTeam);

        // 연락수단
        List<TeamContact> contacts = req.getContacts().stream()
                .map(contact -> TeamContact.builder()
                        .team(savedTeam)
                        .type(contact.getType())
                        .url(contact.getUrl())
                        .build())
                .toList();
        teamContactRepository.saveAll(contacts);

        // 이미지
        if (req.getImageUrls() != null) {
            req.getImageUrls().forEach(imageUrl -> {
                Image image = imageRepository.save(Image.builder().url(imageUrl).build());
                TeamImage teamImage = TeamImage.builder().team(savedTeam).image(image).build();
                teamImageRepository.save(teamImage);
            });
        }

        return savedTeam.getId();
    }


    // 팀 상세조회
    @Transactional(readOnly = true)
    @Override
    public TeamResponse.DetailRes getTeamDetail(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(TeamErrorCode.TEAM_NOT_FOUND));
        User user = team.getUser();

        Optional<UserDepartment> userDepartment = userDepartmentRepository
                .findByUserIdAndType(user.getId(), UserDepartmentType.PRIMARY);
        DepartmentType department = userDepartment.map(ud -> ud.getDepartment().getName())
                .orElse(null);

        // 연락수단
        List<TeamResponse.ContactRes> contacts = teamContactRepository.findAllByTeamId(teamId)
                .stream()
                .map(contact -> TeamResponse.ContactRes.builder()
                        .type(contact.getType())
                        .url(contact.getUrl())
                        .build())
                .toList();

        // 이미지
        List<TeamImage> teamImages = teamImageRepository.findAllByTeamId(teamId);
        List<String> imageUrls = teamImages.stream()
                .map(teamImage -> teamImage.getImage().getUrl())
                .toList();

        TeamResponse.DetailRes detailRes = TeamResponse.DetailRes.builder()
                .teamId(team.getId())
                .title(team.getTitle())
                .startAt(team.getStartAt())
                .endAt(team.getEndAt())
                .maxMember(team.getMaxMember())
                .remainingMember(team.getRemainingMember())
                .userName(user.getName())
                .studentStatus(user.getStatus())
                .department(department)
                .category(team.getCategory())
                .teamStatus(team.getStatus())
                .detail(team.getDetail())
                .contacts(contacts)
                .imageUrls(imageUrls)
                .build();
        return detailRes;
    }

    private TeamResponse.ListRes toListRes(Team team) {
        User user = team.getUser();

        DepartmentType department = userDepartmentRepository
                .findByUserIdAndType(user.getId(), UserDepartmentType.PRIMARY)
                .map(ud -> ud.getDepartment().getName())
                .orElse(null);

        return TeamResponse.ListRes.builder()
                .teamId(team.getId())
                .title(team.getTitle())
                .startAt(team.getStartAt())
                .endAt(team.getEndAt())
                .maxMember(team.getMaxMember())
                .remainingMember(team.getRemainingMember())
                .userName(user.getName())
                .studentStatus(user.getStatus())
                .department(department)
                .category(team.getCategory())
                .teamStatus(team.getStatus())
                .build();
    }

    // 팀 리스트 조회 - RECRUITING 먼저 조회 / 검색 / 카테고리 별 조회
    @Transactional(readOnly = true)
    @Override
    public List<TeamResponse.ListRes> getTeams(TeamCategory category, String keyword) {
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        List<Team> teams = teamRepository.searchTeams(category, keyword);
        return teams.stream().map(this::toListRes).toList();
    }

    @Transactional
    @Override
    public Void deleteTeam(Long userId, Long teamId) {

        Team team =  teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(TeamErrorCode.TEAM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (!team.getUser().getId().equals(user.getId())) {
            throw new BusinessException(TeamErrorCode.WRITER_DIFFERENCE);
        }

        // 연락수단 삭제
        teamContactRepository.deleteAllByTeamId(teamId);

        List<TeamImage> teamImages = teamImageRepository.findAllByTeamId(teamId);
        teamImageRepository.deleteAll(teamImages);
        teamImages.forEach(teamImage -> imageRepository.delete(teamImage.getImage()));

        userTeamRepository.deleteAllByTeamId(teamId);
        teamRepository.delete(team);

        return null;
    }

    @Transactional
    @Override
    public Long updateTeam(Long userId, Long teamId, TeamRequest.UpdateReq req) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(TeamErrorCode.TEAM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (!team.getUser().getId().equals(user.getId())) {
            throw new BusinessException(TeamErrorCode.WRITER_DIFFERENCE);
        }

        if (req.getRemainingMember() > team.getMaxMember()) {
            throw new BusinessException(TeamErrorCode.INVALID_REMAINING_MEMBER);
        }

        team.update(
                req.getTitle(),
                req.getStartAt(),
                req.getEndAt(),
                req.getRemainingMember(),
                req.getCategory(),
                req.getDetail()
        );

        // 연락수단 수정
        teamContactRepository.deleteAllByTeamId(teamId);

        List<TeamContact> contacts = req.getContacts().stream()
                .map(contact -> TeamContact.builder()
                        .team(team)
                        .type(contact.getType())
                        .url(contact.getUrl())
                        .build())
                .toList();
        teamContactRepository.saveAll(contacts);

        // 이미지 수정
        List<TeamImage> teamImages = teamImageRepository.findAllByTeamId(teamId);
        teamImageRepository.deleteAll(teamImages);

        // 기존 Image 삭제
        teamImages.forEach(teamImage ->
                imageRepository.delete(teamImage.getImage()));

        // 이미지 새로 저장
        if (req.getImageUrls() != null) {
            req.getImageUrls().forEach(imageUrl -> {
                Image image = imageRepository.save(Image.builder()
                        .url(imageUrl).build());
                TeamImage teamImage = TeamImage.builder()
                        .team(team)
                        .image(image)
                        .build();
                teamImageRepository.save(teamImage);
            });
        }

        return team.getId();
    }

    //마감일 지난 팀 상태 변경 - 자동 마감
    @Scheduled(fixedRate = 300000) //5분마다 확인
    @Transactional
    @Override
    public void closeExpiredTeams() {
        List<Team> expiredTeams = teamRepository.findByEndAtBeforeAndStatus(LocalDateTime.now(), TeamStatus.RECRUITING);
        expiredTeams.forEach(team -> team.close());
    }

    // 사용자가 작성한 팀 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse.ListRes> getMyTeams(Long userId) {
        List<Team> teams = teamRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return teams.stream()
                .map(this::toListRes)
                .toList();
    }

    // 작성자가 수동 마감
    @Transactional
    @Override
    public Long closeTeam(Long userId, Long teamId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(TeamErrorCode.TEAM_NOT_FOUND));

        if (!team.getUser().getId().equals(user.getId())) {
            throw new BusinessException(TeamErrorCode.WRITER_DIFFERENCE);
        }

        // 이미 마감된 팀인지 확인
        if (team.getStatus() == TeamStatus.CLOSED) {
            throw new BusinessException(TeamErrorCode.TEAM_ALREADY_CLOSED);
        }

        team.close();
        return team.getId();
    }
}
