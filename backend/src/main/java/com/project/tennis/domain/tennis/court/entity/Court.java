package com.project.tennis.domain.tennis.court.entity;

import com.project.tennis.domain.base.BaseTimeEntity;
import com.project.tennis.domain.tennis.court.enums.Environment;
import com.project.tennis.domain.tennis.court.enums.SurfaceType;
import com.project.tennis.domain.tennis.tenniscourt.entity.TennisCourt;
import com.project.tennis.domain.tennis.timeslot.entity.TimeSlot;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Court extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courtCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurfaceType surfaceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    @ManyToOne(fetch = FetchType.LAZY)
    private TennisCourt tennisCourt;

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TimeSlot> timeSlots = new ArrayList<>();
}
