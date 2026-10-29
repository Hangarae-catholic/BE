package com.catholic.hangarae.domain.user.controller;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;
import com.catholic.hangarae.domain.user.service.AuthService;
import com.catholic.hangarae.global.apiPayLoad.response.Response;
import com.catholic.hangarae.global.apiPayLoad.result.UserResultCode;
import com.catholic.hangarae.global.security.annotation.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name="Auth", description = "로그인 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "새로운 회원 등록")
    public ResponseEntity<Response<String>> signUp(@RequestBody @Valid AuthRequest.SignUpReq request) {
        authService.signUp(request);
        return ResponseEntity.status(UserResultCode.USER_SIGNUP_OK.getHttpStatus())
                .body(Response.ok(UserResultCode.USER_SIGNUP_OK, "회원가입이 완료되었습니다. "));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인시 엑세스 토큰과 리프레쉬 토큰을 발급합니다.")
    public Response<TokenRes> login(@RequestBody @Valid AuthRequest.LoginReq request) {
        TokenRes response= authService.login(request);
        return Response.ok(UserResultCode.USER_LOGIN_OK, response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "db의 리프레쉬 토큰을 무효화합니다.")
    public Response<String> logout(@AuthUser Long userId) {
        authService.logout(userId);
        return Response.ok(UserResultCode.USER_LOGOUT_OK, "로그아웃이 완료되었습니다. ");
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원탈퇴", description = "회원을 삭제합니다.")
    public Response<String> withdraw (@AuthUser Long userId) {
        authService.withdraw(userId);
        return Response.ok(UserResultCode.USER_WITHDRAW_OK, "회원탈퇴가 완료되었습니다. ");
    }
}
