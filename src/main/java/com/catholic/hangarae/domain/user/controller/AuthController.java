package com.catholic.hangarae.domain.user.controller;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.request.EmailCodeSendReq;
import com.catholic.hangarae.domain.user.dto.request.FindIdReq;
import com.catholic.hangarae.domain.user.dto.request.FindIdSendReq;
import com.catholic.hangarae.domain.user.dto.request.PasswordResetReq;
import com.catholic.hangarae.domain.user.dto.request.PasswordResetSendReq;
import com.catholic.hangarae.domain.user.dto.request.ReissueReq;
import com.catholic.hangarae.domain.user.dto.request.VerifyCodeReq;
import com.catholic.hangarae.domain.user.dto.response.FindIdRes;
import com.catholic.hangarae.domain.user.dto.response.OAuthLoginRes;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;
import com.catholic.hangarae.domain.user.service.AuthService;
import com.catholic.hangarae.domain.user.vo.OAuthNextStep;
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
    public ResponseEntity<Response<TokenRes>> signUp(@RequestBody @Valid AuthRequest.SignUpReq request) {
        TokenRes response = authService.signUp(request);
        return ResponseEntity.status(UserResultCode.USER_SIGNUP_OK.getHttpStatus())
                .body(Response.ok(UserResultCode.USER_SIGNUP_OK, response));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰과 리프레시 토큰을 새로 발급합니다.")
    public Response<TokenRes> reissue(@RequestBody @Valid ReissueReq request) {
        return Response.ok(UserResultCode.USER_TOKEN_REISSUE_OK, authService.reissue(request.refreshToken()));
    }

    @PostMapping("/verify-code/send")
    @Operation(summary = "회원가입 인증번호 발송", description = "인증번호는 5분간 유효하며, 1분 안에 다시 요청하면 새로 보내지 않습니다.")
    public Response<Void> sendJoinCode(@RequestBody @Valid EmailCodeSendReq request) {
        authService.sendJoinCode(request.email());
        return Response.ok(UserResultCode.USER_VERIFICATION_CODE_SENT, null);
    }

    @PostMapping("/verify-code/confirm")
    @Operation(summary = "인증번호 확인", description = "enum 타입만 바꿔서 넣으면 됩니다.")
    public Response<Void> verifyCode(@RequestBody @Valid VerifyCodeReq request) {
        authService.verifyCode(request);
        return Response.ok(UserResultCode.USER_EMAIL_VERIFIED, null);
    }

    @PostMapping("/find-id/send-code")
    @Operation(summary = "아이디 찾기 인증번호 발송", description = "이름과 이메일이 일치하는 계정에만 발송하며, 응답은 항상 같습니다.")
    public Response<Void> sendFindIdCode(@RequestBody @Valid FindIdSendReq request) {
        authService.sendFindIdCode(request.name(), request.email());
        return Response.ok(UserResultCode.USER_VERIFICATION_CODE_SENT, null);
    }

    @PostMapping("/find-id")
    @Operation(summary = "아이디 찾기")
    public Response<FindIdRes> findId(@RequestBody @Valid FindIdReq request) {
        return Response.ok(UserResultCode.USER_FIND_ID_OK, authService.findId(request));
    }

    @PostMapping("/password-reset/send-code")
    @Operation(summary = "비밀번호 재설정 인증번호 발송", description = "자체 가입 계정에만 발송하며, 응답은 항상 같습니다.")
    public Response<Void> sendPasswordResetCode(@RequestBody @Valid PasswordResetSendReq request) {
        authService.sendPasswordResetCode(request.email());
        return Response.ok(UserResultCode.USER_VERIFICATION_CODE_SENT, null);
    }

    @PostMapping("/password-reset")
    @Operation(summary = "비밀번호 재설정", description = "로그인 제한을 해제하고 기존 로그인 세션을 무효화합니다.")
    public Response<Void> resetPassword(@RequestBody @Valid PasswordResetReq request) {
        authService.resetPassword(request);
        return Response.ok(UserResultCode.USER_PASSWORD_RESET_OK, null);
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

    @GetMapping("/kakao")
    @Operation(summary = "카카오 로그인 시작")
    public ResponseEntity<Void> kakaoLogin() {
        return ResponseEntity
                .status(302)
                .header("Location", authService.getKakaoLoginUrl())
                .build();
    }

    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 로그인 콜백")
    public Response<OAuthLoginRes> kakaoCallback(@RequestParam("code") String code) {
        OAuthLoginRes result = authService.kakaoLogin(code);
        if (result.nextStep() == OAuthNextStep.SIGN_UP) {
            return Response.ok(UserResultCode.USER_KAKAO_SIGNUP_OK, result);
        }
        return Response.ok(UserResultCode.USER_KAKAO_LOGIN_OK, result);
    }

    @GetMapping("/google")
    @Operation(summary = "구글 로그인 시작")
    public ResponseEntity<Void> googleLogin() {
        return ResponseEntity
                .status(302)
                .header("Location", authService.getGoogleLoginUrl())
                .build();
    }

    @GetMapping("/google/callback")
    @Operation(summary = "구글 로그인 콜백")
    public Response<OAuthLoginRes> googleCallback(@RequestParam("code") String code) {
        OAuthLoginRes result = authService.googleLogin(code);
        if (result.nextStep() == OAuthNextStep.SIGN_UP) {
            return Response.ok(UserResultCode.USER_GOOGLE_SIGNUP_OK, result);
        }
        return Response.ok(UserResultCode.USER_GOOGLE_LOGIN_OK, result);
    }
}
