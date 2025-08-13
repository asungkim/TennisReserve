package com.project.tennis.domain.tennis.timeslot.entity;

import com.project.tennis.domain.base.BaseTimeEntity;
import com.project.tennis.domain.tennis.court.entity.Court;
import com.project.tennis.domain.tennis.timeslot.enums.TimeSlotStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class TimeSlot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TimeSlotStatus status = TimeSlotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    private Court court;
}
