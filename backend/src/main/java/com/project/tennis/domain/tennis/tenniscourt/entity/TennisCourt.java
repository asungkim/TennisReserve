package com.project.tennis.domain.tennis.tenniscourt.entity;

import com.project.tennis.domain.base.BaseTimeEntity;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.tennis.court.entity.Court;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class TennisCourt extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String imageUrl;

    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private TennisCourtLocation tennisCourtLocation;

    @OneToMany(mappedBy = "tennisCourt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Court> courts = new ArrayList<>();

    @OneToMany(mappedBy = "tennisCourt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Member> managers = new ArrayList<>();

    public void update(String name, String imageUrl, String phoneNumber) {
        if (name != null) this.name = name;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
    }
}
