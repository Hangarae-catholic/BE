package com.catholic.hangarae.domain.notice.repository;

import com.catholic.hangarae.domain.notice.entity.Notice;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.notice.vo.NoticeCategory;
import com.catholic.hangarae.domain.notice.vo.NoticeType;
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

    // 학과 지정 일정은 계열로, 학과 없는 일정은 JEV 분류와 회원 관심 분야로 조회
    @Query("""
            select n from Notice n
            left join fetch n.department d
            where n.type = :type
              and n.category in :categories
              and (d.division in :divisions
                   or (n.department is null and exists (
                       select ni.id from NoticeInterest ni, UserInterest ui
                       where ni.notice = n
                         and ui.user.id = :userId
                         and ni.interest = ui.interest
                   )))
              and n.startAt < :monthEnd
              and (n.endAt >= :monthStart or (n.endAt is null and n.startAt >= :monthStart))
              and (n.endAt is null or n.endAt >= n.startAt)
            order by n.startAt asc, n.id asc
            """)
    List<Notice> findCalendarNotices(@Param("monthStart") LocalDateTime monthStart,
                                     @Param("monthEnd") LocalDateTime monthEnd,
                                     @Param("type") NoticeType type,
                                     @Param("divisions") List<Division> divisions,
                                     @Param("categories") List<NoticeCategory> categories,
                                     @Param("userId") Long userId);
}
