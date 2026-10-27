package com.catholic.hangarae.domain.notice.repository;

import com.catholic.hangarae.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 선택한 날짜와 행사 기간이 겹치는 목록
    @Query("""
            select n from Notice n
            where n.startAt < :dayEnd
              and (n.endAt >= :dayStart or (n.endAt is null and n.startAt >= :dayStart))
            order by n.startAt asc, n.id asc
            """)
    List<Notice> findByDate(@Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

    @EntityGraph(attributePaths = "department")
    Optional<Notice> findById(Long noticeId);
}
