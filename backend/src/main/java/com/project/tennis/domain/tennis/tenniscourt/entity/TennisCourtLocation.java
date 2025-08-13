package com.project.tennis.domain.tennis.tenniscourt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class TennisCourtLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullAddress;

    private String roadAddress;

    private String region_one_depth;

    private String region_two_depth;

    private String region_three_depth;

    private String zipcode;

    private Double x;

    private Double y;

    @OneToOne(mappedBy = "tennisCourtLocation")
    private TennisCourt court;
}
