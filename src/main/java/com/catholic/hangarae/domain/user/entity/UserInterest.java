package com.catholic.hangarae.domain.user.entity;

import com.catholic.hangarae.domain.interest.entity.Interest;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "user_interests",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_interest_user_interest", columnNames = {"user_id", "interest_id"})
)
@NoArgsConstructor
public class UserInterest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    @Builder
    public UserInterest(User user, Interest interest) {
        this.user = user;
        this.interest = interest;
    }
}
