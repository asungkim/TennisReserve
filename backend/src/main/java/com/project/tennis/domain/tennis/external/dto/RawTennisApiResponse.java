package com.project.tennis.domain.tennis.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RawTennisApiResponse(
        @JsonProperty("ListPublicReservationSport")
        RawReservationSport list
) {
}