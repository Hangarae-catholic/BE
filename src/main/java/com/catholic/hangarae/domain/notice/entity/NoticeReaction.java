package com.catholic.hangarae.domain.notice.entity;

import com.catholic.hangarae.domain.notice.vo.NoticeReactionType;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "notice_reaction",
        uniqueConstraints = @UniqueConstraint(name = "uk_notice_reaction_user_notice", columnNames = {"user_id", "notice_id"})
)
@NoArgsConstructor
public class NoticeReaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeReactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Builder
    public NoticeReaction(NoticeReactionType type, User user, Notice notice) {
        this.type = type;
        this.user = user;
        this.notice = notice;
    }
}
