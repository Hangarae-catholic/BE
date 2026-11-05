package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.request.FindIdReq;
import com.catholic.hangarae.domain.user.dto.request.PasswordResetReq;
import com.catholic.hangarae.domain.user.dto.response.FindIdRes;
import com.catholic.hangarae.domain.user.dto.response.OAuthLoginRes;
import com.catholic.hangarae.domain.user.dto.response.OAuthUserInfo;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;

public interface AuthService {
    TokenRes signUp(AuthRequest.SignUpReq request);
    void checkLoginId(String loginId);
    TokenRes reissue(String refreshToken);

    void sendJoinCode(String email);
    void verifyJoinCode(String email, String code);

    void sendFindIdCode(String name, String email);
    void verifyFindIdCode(String email, String code);
    FindIdRes findId(FindIdReq request);

    void sendPasswordResetCode(String loginId, String email);
    void verifyPasswordResetCode(String email, String code);
    void resetPassword(PasswordResetReq request);

    TokenRes login(AuthRequest.LoginReq request);
    void logout(Long userId);
    void withdraw(Long userId);

    String getKakaoLoginUrl();
    String getKakaoAccessToken(String code);
    OAuthUserInfo getKakaoUserInfo(String accessToken);
    OAuthLoginRes kakaoLogin(String code);

    String getGoogleLoginUrl();
    String getGoogleAccessToken(String code);
    OAuthUserInfo getGoogleUserInfo(String accessToken);
    OAuthLoginRes googleLogin(String code);
}
