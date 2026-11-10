package com.catholic.hangarae.global.config;

import com.catholic.hangarae.global.security.jwt.JwtAuthenticationFilter;
import com.catholic.hangarae.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final Environment environment;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isDev = environment.acceptsProfiles(Profiles.of("dev & !prod"));

        http
                .cors(Customizer.withDefaults()) // cors 기본 구성
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 서버 무상태성 설정
                .csrf(AbstractHttpConfigurer::disable) //csrf 끄기
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 끄기
                .httpBasic(AbstractHttpConfigurer::disable) // http basic 끄기
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/", "/api/auth/sign-up", "/api/auth/login", "/api/auth/reissue").permitAll() // 인증 없이 접근 허용
                            .requestMatchers("/api/auth/verify-code/**", "/api/auth/find-id/**", "/api/auth/password-reset/**").permitAll() // 이메일 인증, 계정 찾기
                            .requestMatchers("/actuator/health", "/actuator/health/**").permitAll() // 배포 상태 확인
                            .requestMatchers("/index.html", "/static/**", "/favicon.ico").permitAll() // 정적 파일 허용
                            .requestMatchers("/swagger", "/swagger/", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 허용
                            .requestMatchers("/api/auth/kakao", "/api/auth/kakao/callback","/api/auth/google","/api/auth/google/callback").permitAll();

                    if (isDev) {
                        auth.anyRequest().permitAll(); // 모든 요청 허용 (테스트용)
                    } else {
                        // shortcut: 프론트 테스트 기간이라 운영도 모든 요청 허용, 테스트 끝나면 authenticated()로 되돌림
                        auth.anyRequest().permitAll();
                    }
                }).addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // 기본 로그인 앞에 jwt 필터 추가
        return http.build();
    }

    // shortcut: 프론트 테스트 기간이라 모든 출처 허용, 프론트 배포 주소가 정해지면 그 주소만 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}