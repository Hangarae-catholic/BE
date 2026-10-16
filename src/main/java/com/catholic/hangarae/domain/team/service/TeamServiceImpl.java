package com.catholic.hangarae.domain.team.service;


import com.catholic.hangarae.domain.department.entity.Department;
import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.image.entity.Image;
import com.catholic.hangarae.domain.image.repository.ImageRepository;
import com.catholic.hangarae.domain.team.dto.request.TeamRequest;
import com.catholic.hangarae.domain.team.dto.response.TeamResponse;
import com.catholic.hangarae.domain.team.entity.Team;
import com.catholic.hangarae.domain.team.entity.TeamImage;
import com.catholic.hangarae.domain.team.entity.UserTeam;
import com.catholic.hangarae.domain.team.repository.TeamImageRepository;
import com.catholic.hangarae.domain.team.repository.TeamRepository;
import com.catholic.hangarae.domain.team.repository.UserTeamRepository;
import com.catholic.hangarae.domain.team.vo.UserTeamRole;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.user.entity.UserDepartment;
import com.catholic.hangarae.domain.user.repository.UserDepartmentRepository;
import com.catholic.hangarae.domain.user.repository.UserRepository;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import com.catholic.hangarae.global.apiPayLoad.error.TeamErrorCode;
import com.catholic.hangarae.global.apiPayLoad.error.UserErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .contactUrl(req.getContactUrl())
                .user(user)
                .build();
        Team savedTeam = teamRepository.save(team);

        UserTeam userTeam = UserTeam.builder()
                .user(user)
                .team(team)
                .role(UserTeamRole.LEADER)
                .build();
        userTeamRepository.save(userTeam);

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

    @Override
    public TeamResponse.DetailRes getTeamDetail(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(TeamErrorCode.TEAM_NOT_FOUND));
        User user = team.getUser();

        Optional<UserDepartment> userDepartment = userDepartmentRepository
                .findByUserIdAndType(user.getId(), UserDepartmentType.PRIMARY);
        DepartmentType department = userDepartment.map(ud -> ud.getDepartment().getName())
                .orElse(null);

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
                .userName(user.getName())
                .studentStatus(user.getStatus())
                .department(department)
                .category(team.getCategory())
                .teamStatus(team.getStatus())
                .detail(team.getDetail())
                .contactUrl(team.getContactUrl())
                .imageUrls(imageUrls)
                .build();
        return detailRes;
    }


}
