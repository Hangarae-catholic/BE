package com.catholic.hangarae.domain.user.controller;

import com.catholic.hangarae.domain.user.dto.request.ProfileReq;
import com.catholic.hangarae.domain.user.dto.response.ProfileOptionsRes;
import com.catholic.hangarae.domain.user.dto.response.ProfileRes;
import com.catholic.hangarae.domain.user.service.UserService;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.UserResultCode;
import com.catholic.hangarae.global.security.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "User", description = "프로필 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/profile")
    @Operation(summary = "프로필 생성")
    public ResponseEntity<Response<ProfileRes>> createProfile(
            @AuthUser Long userId, @RequestBody @Valid ProfileReq request) {
        ProfileRes response = userService.createProfile(userId, request);
        return ResponseEntity.status(UserResultCode.USER_PROFILE_CREATE_OK.getHttpStatus())
                .body(Response.ok(UserResultCode.USER_PROFILE_CREATE_OK, response));
    }

    @GetMapping("/profile")
    @Operation(summary = "내 프로필 조회")
    public Response<ProfileRes> getProfile(@AuthUser Long userId) {
        return Response.ok(UserResultCode.USER_PROFILE_GET_OK, userService.getProfile(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "프로필 수정", description = "전체 프로필을 전달하며, 목록에서 뺀 추가 전공과 관심 분야는 삭제합니다.")
    public Response<ProfileRes> updateProfile(
            @AuthUser Long userId, @RequestBody @Valid ProfileReq request) {
        ProfileRes response = userService.updateProfile(userId, request);
        return Response.ok(UserResultCode.USER_PROFILE_UPDATE_OK, response);
    }

    @GetMapping("/nickname/check")
    @Operation(summary = "닉네임 중복 확인", description = "현재 본인의 닉네임은 사용 가능합니다.")
    public Response<Void> checkNickname(
            @AuthUser Long userId, @RequestParam String nickname) {
        userService.checkNickname(userId, nickname);
        return Response.ok(UserResultCode.USER_NICKNAME_CHECK_OK, null);
    }

    @GetMapping("/profile/check")
    @Operation(summary = "프로필 생성 여부 확인", description = "로그인 후 프로필 존재 여부 확인.")
    public Response<Boolean> checkProfile(@AuthUser Long userId) {
        return Response.ok(UserResultCode.USER_PROFILE_CHECK_OK, userService.checkProfile(userId));
    }

    @GetMapping("/profile/options")
    @Operation(summary = "프로필 선택 항목 조회", description = "캐릭터, 학적, 계열별 학과와 관심 분야 목록을 조회합니다.")
    public Response<ProfileOptionsRes> getProfileOptions() {
        return Response.ok(UserResultCode.USER_PROFILE_OPTIONS_OK, userService.getProfileOptions());
    }
}
