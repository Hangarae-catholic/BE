package com.catholic.hangarae.domain.user.service;

import com.catholic.hangarae.domain.user.dto.response.OAuthLoginRes;

public interface OAuthTicketService {

    String createTicket(OAuthLoginRes loginResult);
    OAuthLoginRes exchangeTicket(String ticket);
}
