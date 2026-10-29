package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;

public interface AuthService {
    void signUp(AuthRequest.SignUpReq request);
    TokenRes login(AuthRequest.LoginReq request);
    void logout(Long userId);
    void withdraw(Long userId);
}
