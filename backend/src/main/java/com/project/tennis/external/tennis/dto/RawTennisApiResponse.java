package com.project.tennis.external.tennis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RawTennisApiResponse(
        @JsonProperty("ListPublicReservationSport")
        RawReservationSport list
) {
}