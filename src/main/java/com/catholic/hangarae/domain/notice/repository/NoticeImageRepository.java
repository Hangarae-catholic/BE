package com.catholic.hangarae.domain.notice.repository;

import com.catholic.hangarae.domain.notice.entity.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

    @EntityGraph(attributePaths = "image")
    List<NoticeImage> findAllByNoticeIdOrderByIdAsc(Long noticeId);
}
