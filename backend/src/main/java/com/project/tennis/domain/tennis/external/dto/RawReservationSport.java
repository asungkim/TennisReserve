package com.project.tennis.domain.tennis.external.dto;

import java.util.List;

public record RawReservationSport(
        RawResult RESULT,
        List<RawCourt> row
) {
}
