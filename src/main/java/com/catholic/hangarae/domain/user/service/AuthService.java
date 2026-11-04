package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.response.OAuthLoginRes;
import com.catholic.hangarae.domain.user.dto.response.OAuthUserInfo;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;

public interface AuthService {
    void signUp(AuthRequest.SignUpReq request);
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
