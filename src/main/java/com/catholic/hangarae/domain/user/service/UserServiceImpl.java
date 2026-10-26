package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.department.entity.Department;
import com.catholic.hangarae.domain.department.repository.DepartmentRepository;
import com.catholic.hangarae.domain.interest.entity.Interest;
import com.catholic.hangarae.domain.interest.repository.InterestRepository;
import com.catholic.hangarae.domain.user.dto.request.UserRequest;
import com.catholic.hangarae.domain.user.dto.response.UserResponse;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.user.entity.UserDepartment;
import com.catholic.hangarae.domain.user.entity.UserInterest;
import com.catholic.hangarae.domain.user.repository.UserDepartmentRepository;
import com.catholic.hangarae.domain.user.repository.UserInterestRepository;
import com.catholic.hangarae.domain.user.repository.UserRepository;
import com.catholic.hangarae.domain.user.vo.UserDepartmentType;
import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import com.catholic.hangarae.global.apiPayLoad.error.UserErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDepartmentRepository userDepartmentRepository;
    private final UserInterestRepository userInterestRepository;
    private final DepartmentRepository departmentRepository;
    private final InterestRepository interestRepository;

    @Override
    @Transactional
    public UserResponse.ProfileRes createProfile(Long userId, UserRequest.ProfileReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        if (user.getCharacter() != null) {
            throw new BusinessException(UserErrorCode.PROFILE_ALREADY_EXIST);
        }
        saveProfile(user, request);
        return toProfileRes(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse.ProfileRes getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        checkProfileExists(user);
        return toProfileRes(user);
    }

    @Override
    @Transactional
    public UserResponse.ProfileRes updateProfile(Long userId, UserRequest.ProfileReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        checkProfileExists(user);
        saveProfile(user, request);
        return toProfileRes(user);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkNickname(Long userId, String nickname) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }
        validateNickname(nickname);
        if (userRepository.existsByNicknameAndIdNot(nickname, userId)) {
            throw new BusinessException(UserErrorCode.NICKNAME_ALREADY_EXIST);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse.ProfileOptionsRes getProfileOptions() {
        // 선택 가능한 학과
        List<UserResponse.DepartmentRes> departments = new ArrayList<>();
        for (Department department : departmentRepository.findAll()) {
            departments.add(UserResponse.DepartmentRes.builder()
                    .departmentId(department.getId())
                    .name(department.getName())
                    .division(department.getDivision())
                    .build());
        }

        // 선택 가능한 관심 분야
        List<UserResponse.InterestRes> interests = new ArrayList<>();
        for (Interest interest : interestRepository.findAll()) {
            interests.add(UserResponse.InterestRes.builder()
                    .interestId(interest.getId())
                    .name(interest.getName())
                    .build());
        }
        return UserResponse.ProfileOptionsRes.builder()
                .characters(Arrays.asList(UserCharacter.values()))
                .statuses(Arrays.asList(StudentStatus.values()))
                .departments(departments)
                .interests(interests)
                .build();
    }

    private void saveProfile(User user, UserRequest.ProfileReq request) {
        validateNickname(request.getNickname());
        if (userRepository.existsByNicknameAndIdNot(request.getNickname(), user.getId())) {
            throw new BusinessException(UserErrorCode.NICKNAME_ALREADY_EXIST);
        }
        // 전공 조회와 검증
        Department primary = findDepartment(request.getPrimaryDepartmentId());
        Department doubleMajor = findDepartment(request.getDoubleMajorDepartmentId());
        Department minor = findDepartment(request.getMinorDepartmentId());
        validateDepartments(primary, doubleMajor, minor);
        List<Interest> interests = validateInterests(request.getInterestIds());

        // 목록에서 제외한 추가 전공과 관심 분야는 삭제
        userDepartmentRepository.deleteAllByUserId(user.getId());
        userInterestRepository.deleteAllByUserId(user.getId());
        user.updateProfile(request.getNickname(), request.getCharacter(), request.getStatus());
        userRepository.saveAndFlush(user);
        // 전공 구분은 서버에서 지정
        saveDepartment(user, primary, UserDepartmentType.PRIMARY);
        saveDepartment(user, doubleMajor, UserDepartmentType.DOUBLE_MAJOR);
        saveDepartment(user, minor, UserDepartmentType.MINOR);
        for (Interest interest : interests) {
            userInterestRepository.save(UserInterest.builder()
                    .user(user)
                    .interest(interest)
                    .build());
        }
    }

    private Department findDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.DEPARTMENT_NOT_FOUND));
    }

    private void validateDepartments(Department primary, Department doubleMajor, Department minor) {
        if (primary == null) {
            throw new BusinessException(UserErrorCode.INVALID_DEPARTMENTS);
        }
        // 같은 학과 중복 선택 방지
        if ((doubleMajor != null && primary.getId().equals(doubleMajor.getId()))
                || (minor != null && primary.getId().equals(minor.getId()))
                || (doubleMajor != null && minor != null && doubleMajor.getId().equals(minor.getId()))) {
            throw new BusinessException(UserErrorCode.INVALID_DEPARTMENTS);
        }
        // 세 전공의 계열이 모두 다르면 선택 불가
        if (doubleMajor != null && minor != null
                && primary.getDivision() != doubleMajor.getDivision()
                && primary.getDivision() != minor.getDivision()
                && doubleMajor.getDivision() != minor.getDivision()) {
            throw new BusinessException(UserErrorCode.INVALID_DEPARTMENTS);
        }
    }

    private void saveDepartment(User user, Department department, UserDepartmentType type) {
        if (department != null) {
            userDepartmentRepository.save(UserDepartment.builder()
                    .user(user)
                    .department(department)
                    .type(type)
                    .build());
        }
    }

    private List<Interest> validateInterests(List<Long> interestIds) {
        List<Interest> result = new ArrayList<>();
        for (Long interestId : interestIds) {
            // 같은 관심 분야 중복 선택 방지
            if (Collections.frequency(interestIds, interestId) > 1) {
                throw new BusinessException(UserErrorCode.INVALID_INTERESTS);
            }
            Interest interest = interestRepository.findById(interestId)
                    .orElseThrow(() -> new BusinessException(UserErrorCode.INTEREST_NOT_FOUND));
            result.add(interest);
        }
        return result;
    }

    private UserResponse.ProfileRes toProfileRes(User user) {
        // 저장된 전공 정보
        List<UserResponse.DepartmentRes> departments = new ArrayList<>();
        for (UserDepartment item : userDepartmentRepository.findAllByUserIdOrderByIdAsc(user.getId())) {
            Department department = item.getDepartment();
            departments.add(UserResponse.DepartmentRes.builder()
                    .departmentId(department.getId())
                    .name(department.getName())
                    .division(department.getDivision())
                    .type(item.getType())
                    .build());
        }

        // 저장된 관심 분야
        List<UserResponse.InterestRes> interests = new ArrayList<>();
        for (UserInterest item : userInterestRepository.findAllByUserIdOrderByIdAsc(user.getId())) {
            interests.add(UserResponse.InterestRes.builder()
                    .interestId(item.getInterest().getId())
                    .name(item.getInterest().getName())
                    .build());
        }
        return UserResponse.ProfileRes.builder()
                .userId(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .character(user.getCharacter())
                .status(user.getStatus())
                .departments(departments)
                .interests(interests)
                .build();
    }

    private void checkProfileExists(User user) {
        if (user.getCharacter() == null) {
            throw new BusinessException(UserErrorCode.PROFILE_NOT_FOUND);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || !NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new BusinessException(UserErrorCode.INVALID_NICKNAME_FORMAT);
        }
    }

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\p{L}\\p{N}]{6,255}$");
}
