package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.UserRequest;
import com.catholic.hangarae.domain.user.dto.response.UserResponse;

public interface UserService {

    UserResponse.ProfileRes createProfile(Long userId, UserRequest.ProfileReq request);
    UserResponse.ProfileRes getProfile(Long userId);
    UserResponse.ProfileRes updateProfile(Long userId, UserRequest.ProfileReq request);
    void checkNickname(Long userId, String nickname);
    UserResponse.ProfileOptionsRes getProfileOptions();
}
