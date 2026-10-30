package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.request.AuthRequest;
import com.catholic.hangarae.domain.user.dto.response.KakaoLoginRes;
import com.catholic.hangarae.domain.user.dto.response.KakaoUserInfo;
import com.catholic.hangarae.domain.user.dto.response.TokenRes;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.user.entity.Provider;
import com.catholic.hangarae.domain.user.repository.ProviderRepository;
import com.catholic.hangarae.domain.user.repository.UserRepository;
import com.catholic.hangarae.domain.user.vo.AuthProvider;
import com.catholic.hangarae.domain.user.vo.KakaoNextStep;
import com.catholic.hangarae.global.apiPayLoad.error.AuthErrorCode;
import com.catholic.hangarae.global.apiPayLoad.error.UserErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import com.catholic.hangarae.global.security.jwt.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EntityManager entityManager;
    private final ProviderRepository providerRepository;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.client-secret:}")
    private String kakaoClientSecret;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                           EntityManager entityManager, ProviderRepository providerRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.entityManager = entityManager;
        this.providerRepository = providerRepository;
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
        User user=userRepository.findUserByLoginId(request.getLoginId())
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
        User user=userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateRefreshToken(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void withdraw(Long userId) {
        User user=userRepository.findById(userId)
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


    @Override
    public String getKakaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + kakaoClientId
                + "&redirect_uri=" + URLEncoder.encode(
                kakaoRedirectUri,
                StandardCharsets.UTF_8
        );
    }

    @Override
    public String getKakaoAccessToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", kakaoClientId);
        form.add("redirect_uri", kakaoRedirectUri);
        form.add("code", code);

        if (kakaoClientSecret != null && !kakaoClientSecret.isBlank()) {
            form.add("client_secret", kakaoClientSecret);
        }

        Map<String, Object> response = RestClient.create()
                .post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (response == null || !(response.get("access_token") instanceof String accessToken)) {
            throw new BusinessException(AuthErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        }

        return accessToken;
    }


    @Override
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {

        Map<String, Object> response = RestClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (response == null || response.get("id") == null) {
            throw new BusinessException(AuthErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
        }

        String providerId = String.valueOf(response.get("id"));

        Map<?, ?> kakaoAccount =
                response.get("kakao_account") instanceof Map<?, ?> account
                        ? account : Map.of();
        String email = kakaoAccount.get("email") instanceof String value
                ? value : null;

        return new KakaoUserInfo(providerId, email);
    }


    @Override
    @Transactional
    public KakaoLoginRes kakaoLogin(String code) {
        String kakaoAccessToken = getKakaoAccessToken(code);

        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);
        String providerId = kakaoUserInfo.providerId();
        String email = kakaoUserInfo.email();

        // 기존 카카오 회원 조회
        Provider provider = providerRepository
                .findByProviderAndProviderId(AuthProvider.KAKAO.name(), providerId)
                .orElse(null);

        User user;
        KakaoNextStep nextStep;

        if (provider != null) {
            // 기존 회원
            user = provider.getUser();
            nextStep = KakaoNextStep.LOGIN;

        } else {
            // 신규 회원
            if (email == null || email.isBlank()) {
                throw new BusinessException(AuthErrorCode.KAKAO_EMAIL_NOT_PROVIDED);
            }

            // 이메일 중복 확인
            if (userRepository.existsByEmail(email)) {
                throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXIST);
            }

            user = User.builder()
                    .email(email)
                    .build();
            userRepository.save(user);

            Provider newProvider = Provider.builder()
                    .provider(AuthProvider.KAKAO.name())
                    .providerId(providerId)
                    .user(user)
                    .build();
            providerRepository.save(newProvider);

            nextStep = KakaoNextStep.SIGN_UP;
        }

        // 행가래 JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(user, AuthProvider.KAKAO);
        String refreshToken = jwtTokenProvider.createRefreshToken(user, AuthProvider.KAKAO);
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        // 결과 반환
        return new KakaoLoginRes(
                nextStep,
                nextStep == KakaoNextStep.SIGN_UP ? email : null,
                new TokenRes(accessToken, refreshToken)
        );
    }
}
