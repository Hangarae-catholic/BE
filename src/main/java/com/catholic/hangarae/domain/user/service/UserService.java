package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.ProfileReq;
import com.catholic.hangarae.domain.user.dto.response.ProfileOptionsRes;
import com.catholic.hangarae.domain.user.dto.response.ProfileRes;

public interface UserService {

    ProfileRes createProfile(Long userId, ProfileReq request);
    ProfileRes getProfile(Long userId);
    ProfileRes updateProfile(Long userId, ProfileReq request);
    void checkNickname(Long userId, String nickname);

    boolean checkProfile(Long userId);
    ProfileOptionsRes getProfileOptions();
}
