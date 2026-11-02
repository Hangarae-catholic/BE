package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.response.KakaoLoginRes;
import com.catholic.hangarae.domain.user.dto.response.KakaoUserInfo;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;

public interface AuthService {
    void signUp(AuthRequest.SignUpReq request);
    TokenRes login(AuthRequest.LoginReq request);
    void logout(Long userId);
    void withdraw(Long userId);

    String getKakaoLoginUrl();
    String getKakaoAccessToken(String code);
    KakaoUserInfo getKakaoUserInfo(String accessToken);
    KakaoLoginRes kakaoLogin(String code);

}
