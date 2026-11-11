
package com.catholic.hangarae.domain.user.repository;

import com.catholic.hangarae.domain.user.entity.OAuthTicket;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OAuthTicketRepository
        extends JpaRepository<OAuthTicket, Long> {

    Optional<OAuthTicket> findByTicket(String ticket);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from OAuthTicket t where t.ticket = :ticket")
    Optional<OAuthTicket> findByTicketForUpdate(
            @Param("ticket") String ticket
    );
}
