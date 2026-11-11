package com.catholic.hangarae.domain.notice.repository;

import com.catholic.hangarae.domain.notice.entity.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

    @EntityGraph(attributePaths = "image")
    List<NoticeImage> findAllByNoticeIdOrderByIdAsc(Long noticeId);

    // 목록의 행사 이미지를 한 번에 조회
    @EntityGraph(attributePaths = "image")
    List<NoticeImage> findAllByNoticeIdInOrderByIdAsc(List<Long> noticeIds);
}
