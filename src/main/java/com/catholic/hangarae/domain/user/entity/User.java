package com.catholic.hangarae.domain.user.entity;

import com.catholic.hangarae.domain.user.vo.StudentStatus;
import com.catholic.hangarae.domain.user.vo.UserCharacter;
import com.catholic.hangarae.global.apiPayLoad.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(unique = true)
    private String nickname;

    private String name;
    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_character")
    private UserCharacter character;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Builder
    public User(String nickname, String name, String email, UserCharacter character, StudentStatus status, String loginId, String password) {
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.character = character;
        this.status = status;
        this.loginId = loginId;
        this.password = password;
    }
}
