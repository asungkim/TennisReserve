package com.project.tennis.domain.tennis.tenniscourt.dto.response;

import com.project.tennis.domain.tennis.tenniscourt.entity.TennisCourtLocation;
import com.project.tennis.domain.tennis.tenniscourt.entity.TennisCourt;
import lombok.Builder;

@Builder
public record TennisCourtResponse(
        Long id,
        String name,
        String imageUrl,
        String phoneNumber,
        TennisCourtLocation tennisCourtLocation
) {
    public static TennisCourtResponse from(TennisCourt tennisCourt) {
        return TennisCourtResponse.builder()
                .id(tennisCourt.getId())
                .name(tennisCourt.getName())
                .imageUrl(tennisCourt.getImageUrl())
                .phoneNumber(tennisCourt.getPhoneNumber())
                .tennisCourtLocation(tennisCourt.getTennisCourtLocation())
                .build();
    }
}
