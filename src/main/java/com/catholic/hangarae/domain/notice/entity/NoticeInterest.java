package com.catholic.hangarae.domain.notice.entity;

import com.catholic.hangarae.domain.interest.entity.Interest;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notice_interests")
@NoArgsConstructor
public class NoticeInterest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Builder
    public NoticeInterest(Double score, Interest interest, Notice notice) {
        this.score = score;
        this.interest = interest;
        this.notice = notice;
    }
}
