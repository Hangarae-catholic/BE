package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.user.repository.UserRepository;
import com.catholic.hangarae.domain.user.vo.AuthProvider;
import com.catholic.hangarae.global.apiPayLoad.error.AuthErrorCode;
import com.catholic.hangarae.global.apiPayLoad.error.UserErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EntityManager entityManager;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                           EntityManager entityManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void signUp(AuthRequest.SignUpReq request) {
        validateSignUpRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXIST);
        }

        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(AuthErrorCode.LOGIN_ID_ALREADY_EXIST);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(AuthErrorCode.NICKNAME_ALREADY_EXIST);
        }

        String encodedPassword=passwordEncoder.encode(request.getPassword());

        User user=User.createLocalUser(
                request.getLoginId(),
                request.getName(),
                request.getNickname(),
                request.getEmail(),
                encodedPassword
        );

        userRepository.save(user);
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public TokenRes login(AuthRequest.LoginReq request) {
        User user=userRepository.findByLoginIdForUpdate(request.getLoginId())
                .orElseThrow(()-> new BusinessException(AuthErrorCode.PASSWORD_UNMATCH_ERROR));

        if (user.isLoginRestricted()) {
            throw new BusinessException(AuthErrorCode.LOGIN_RESTRICTED);
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.recordLoginFailure();
            if (user.isLoginRestricted()) {
                throw new BusinessException(AuthErrorCode.LOGIN_RESTRICTED);
            }
            throw new BusinessException(AuthErrorCode.PASSWORD_UNMATCH_ERROR);
        }

        String accessToken=jwtTokenProvider.createAccessToken(user,AuthProvider.LOCAL);
        String refreshToken=jwtTokenProvider.createRefreshToken(user, AuthProvider.LOCAL);

        user.resetLoginFailures();
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return new TokenRes(accessToken,refreshToken);
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        User user=userRepository.findByIdForUpdate(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateRefreshToken(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void withdraw(Long userId) {
        User user=userRepository.findByIdForUpdate(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 팀 게시글은 작성자 정보만 비우고 유지
        entityManager.createQuery("update Team t set t.user = null where t.user.id = :userId")
                .setParameter("userId", userId).executeUpdate();
        for (String entity : new String[]{"UserDepartment", "UserInterest", "Provider", "NoticeReaction", "UserTeam"}) {
            entityManager.createQuery("delete from " + entity + " e where e.user.id = :userId")
                    .setParameter("userId", userId).executeUpdate();
        }
        userRepository.delete(user);
    }


    private void validateSignUpRequest(AuthRequest.SignUpReq request) {

        if (!LOGIN_ID_PATTERN.matcher(request.getLoginId()).matches()) {
            throw new BusinessException(AuthErrorCode.INVALID_LOGIN_ID_FORMAT);
        }

        if (!NICKNAME_PATTERN.matcher(request.getNickname()).matches()) {
            throw new BusinessException(AuthErrorCode.INVALID_NICKNAME_FORMAT);
        }

        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            throw new BusinessException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(AuthErrorCode.CONFIRM_PASSWORD_MISMATCH);
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BusinessException(AuthErrorCode.INVAILD_EMAIL_FORMAT);
        }
    }

    private static final Pattern LOGIN_ID_PATTERN =
            Pattern.compile("^[A-Za-z0-9]{6,255}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N}]{6,255}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[\\p{P}\\p{S}])[^\\s]{8,16}$");


}
