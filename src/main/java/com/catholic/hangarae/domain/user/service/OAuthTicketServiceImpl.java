
package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.response.OAuthLoginRes;
import com.catholic.hangarae.domain.user.entity.OAuthTicket;
import com.catholic.hangarae.domain.user.repository.OAuthTicketRepository;
import com.catholic.hangarae.global.apiPayLoad.error.AuthErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import org.springframework.scheduling.annotation.Scheduled;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OAuthTicketServiceImpl implements OAuthTicketService {

    private final OAuthTicketRepository oauthTicketRepository;
    private final ObjectMapper objectMapper;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String createTicket(OAuthLoginRes loginResult) {

        // 티켓 생성
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String ticket = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // 로그인 결과를 JSON 문자열로 변환
        String payload;

        try {
            payload = objectMapper.writeValueAsString(loginResult);
        } catch (JacksonException e) {
            throw new BusinessException(AuthErrorCode.OAUTH_TICKET_CREATE_FAILED);
        }

        // 티켓 만료 시간 설정 (2분)
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(2);

        // DB 저장
        OAuthTicket oauthTicket = OAuthTicket.builder()
                .ticket(ticket)
                .payload(payload)
                .expiresAt(expiresAt)
                .build();
        oauthTicketRepository.save(oauthTicket);

        // 프론트엔드에 티켓 반환
        return ticket;
    }

    @Transactional
    @Override
    public OAuthLoginRes exchangeTicket(String ticket) {

        OAuthTicket oauthTicket = oauthTicketRepository
                .findByTicketForUpdate(ticket)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.OAUTH_TICKET_INVALID));

        // 만료 확인
        if (oauthTicket.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(AuthErrorCode.OAUTH_TICKET_EXPIRED);
        }

        // 티켓 사용 후 삭제
        String payload = oauthTicket.getPayload();
        oauthTicketRepository.delete(oauthTicket);

        try {
            return objectMapper.readValue(payload, OAuthLoginRes.class);
        } catch (JacksonException e) {
            throw new BusinessException(AuthErrorCode.OAUTH_TICKET_PARSE_FAILED);
        }
    }

    // 1시간마다 만료 티켓 정리
    @Scheduled(fixedDelay = 3600000)
    public void cleanupExpiredTickets() {
        oauthTicketRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
