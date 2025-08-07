package com.project.tennis.external.tennis.dto;

import java.util.List;

public record RawReservationSport(
        RawResult RESULT,
        List<RawCourt> row
) {
}
