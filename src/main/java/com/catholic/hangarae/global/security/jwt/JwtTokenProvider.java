package com.catholic.hangarae.global.security.jwt;

import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.user.vo.AuthProvider;
import com.catholic.hangarae.global.apiPayLoad.error.AuthErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private JwtParser jwtParser; // 토큰 파싱 및 검증 객체
    private SecretKey secretKey; // jwt 서명시 사용할 키

    // 환경변수 주입 후 생성하고자 init 사용
    @PostConstruct
    public void init() {

        // jwt secret 문자열을 디코딩하여 key 객체로 변환
        try {
            this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
            if (secretKey.getEncoded().length < 64 || accessTokenValidity <= 0 || refreshTokenValidity <= 0) {
                throw new BusinessException(AuthErrorCode.JWT_CONFIGURATION_ERROR);
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.JWT_CONFIGURATION_ERROR);
        }

        // 토큰 검증 및 파싱에 사용
        // secretKey로 서명한 토큰만 유효하게 설정
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    // accessToken 생성
    public String createAccessToken(User user, AuthProvider authProvider) {
        try {
            return generateToken(user, authProvider,accessTokenValidity,"access");
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.JWT_GENERATED_FAILED);
        }
    }


    // refreshToken 생성
    public String createRefreshToken(User user, AuthProvider authProvider) {
        try {
            return generateToken(user, authProvider,refreshTokenValidity,"refresh");
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.JWT_GENERATED_FAILED);
        }
    }

    // jwt 토큰 생성
    private String generateToken(User user, AuthProvider authProvider, long validity, String category) {
        Date now=new Date(); // 현재시간
        Date expiryDate=new Date(now.getTime()+validity); // 만료시간

        return Jwts.builder()
                .subject(user.getId().toString()) // 사용자 id를 식별자로 설정
                .claim("AuthProvider",authProvider) // 로그인 방법, LOCAL, GOOGLE, KAKAO
                .claim("category",category) // access인지 refresh인지
                .issuedAt(now) // 토큰 발급시간
                .expiration(expiryDate) // 만료시간
                .signWith(getSigningKey(),Jwts.SIG.HS512) // 시크릿 키 및 서명방식
                .compact();
    }

    // jwt에서 claim 객체 추출
    public Claims parseClaims(String token) {
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(AuthErrorCode.JWT_EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.JWT_INVALID_TOKEN);
        }
    }

    // 서명에 사용한 시크릿 키 반환
    private SecretKey getSigningKey() {
        return this.secretKey;
    }


    // token에서 사용자 id 추출,
    // @AuthUser랑 JwtAuthenticationFilter에서 사용
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException | NullPointerException e) {
            throw new BusinessException(AuthErrorCode.JWT_INVALID_TOKEN);
        }
    }

    public AuthProvider getAuthProviderFromToken(String bearertoken) {

        String token=extractBearerToken(bearertoken);
        Claims claims = parseClaims(token);
        try {
            return AuthProvider.valueOf(claims.get("AuthProvider", String.class));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException(AuthErrorCode.JWT_INVALID_TOKEN);
        }
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    public String extractBearerToken(String bearerToken) {
        if (bearerToken == null) {
            throw new BusinessException(AuthErrorCode.AUTHORIZATION_HEADER_NOT_FOUND);
        }

        if (!bearerToken.startsWith("Bearer ")) {
            throw new BusinessException(AuthErrorCode.INVALID_AUTHORIZATION_FORMAT);
        }

        String token = bearerToken.substring(7);

        if (token.trim().isEmpty()) {
            throw new BusinessException(AuthErrorCode.JWT_TOKEN_NOT_FOUND);
        }

        return token;
    }

    public boolean isExpired(String token) {
        try {
            Claims claims= parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (BusinessException e) {
            if (e.getErrorCode() == AuthErrorCode.JWT_EXPIRED_TOKEN) {
                return true;
            }
            throw e;
        }
    }

    public boolean isValid(String token) {
        try {
            validateToken(token);
            return true;
        }
        catch( BusinessException e) {
            return false;
        }
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            if (!"access".equals(claims.get("category", String.class))) {
                throw new BusinessException(AuthErrorCode.JWT_INVALID_TOKEN);
            }
            if (claims.getExpiration() == null) {
                throw new BusinessException(AuthErrorCode.JWT_INVALID_TOKEN);
            }
            return true;
        } catch( ExpiredJwtException e) {
            throw new BusinessException(AuthErrorCode.JWT_EXPIRED_TOKEN);
        } catch( JwtException | IllegalArgumentException | NullPointerException e) {
            throw new BusinessException(AuthErrorCode.JWT_INVALID_TOKEN);
        }
    }

}
