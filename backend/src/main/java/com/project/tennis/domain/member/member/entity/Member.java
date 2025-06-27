package com.project.tennis.domain.member.member.entity;

import com.project.tennis.domain.base.BaseTimeEntity;
import com.project.tennis.domain.member.member.entity.enums.Role;
import com.project.tennis.domain.member.member.entity.enums.SocialProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Member extends BaseTimeEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider provider;

}
