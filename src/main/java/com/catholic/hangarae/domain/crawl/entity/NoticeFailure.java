package com.catholic.hangarae.domain.crawl.entity;

import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수집, 분류에 실패한 공지, 성공하면 삭제
@Entity
@Getter
@Table(name = "notice_failures", indexes = @Index(name = "idx_notice_failure_url", columnList = "url"))
@NoArgsConstructor
public class NoticeFailure extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 500, nullable = false)
    private String url;

    // 수집 게시판 키
    @Column(length = 120)
    private String sourceKey;

    // 마지막 실패 사유, 에러 코드 또는 예외 이름
    @Column(length = 64)
    private String reason;

    private int attempts;

    @Builder
    public NoticeFailure(String url, String sourceKey) {
        this.url = url;
        this.sourceKey = sourceKey;
    }

    public void fail(String reason) {
        this.reason = reason;
        this.attempts++;
    }
}
