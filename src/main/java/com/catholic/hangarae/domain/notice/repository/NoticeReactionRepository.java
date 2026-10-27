package com.catholic.hangarae.domain.notice.repository;

import com.catholic.hangarae.domain.notice.entity.NoticeReaction;
import com.catholic.hangarae.domain.notice.vo.NoticeReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface NoticeReactionRepository extends JpaRepository<NoticeReaction, Long> {

    Optional<NoticeReaction> findByUserIdAndNoticeId(Long userId, Long noticeId);
    Long countByNoticeIdAndType(Long noticeId, NoticeReactionType type);

}

