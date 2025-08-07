package com.project.tennis.domain.tennis.court.dto;

import com.project.tennis.domain.tennis.court.entity.CourtLocation;
import com.project.tennis.domain.tennis.court.entity.TennisCourt;
import lombok.Builder;

@Builder
public record TennisCourtResponse(
        Long id,
        String name,
        String imageUrl,
        String phoneNumber,
        CourtLocation courtLocation
) {
    public static TennisCourtResponse from(TennisCourt tennisCourt) {
        return TennisCourtResponse.builder()
                .id(tennisCourt.getId())
                .name(tennisCourt.getName())
                .imageUrl(tennisCourt.getImageUrl())
                .phoneNumber(tennisCourt.getPhoneNumber())
                .courtLocation(tennisCourt.getCourtLocation())
                .build();
    }
}
