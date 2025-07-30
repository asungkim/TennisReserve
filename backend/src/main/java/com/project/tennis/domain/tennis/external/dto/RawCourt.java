package com.project.tennis.domain.tennis.external.dto;

public record RawCourt(
        String SVCID,
        String SVCNM,
        String PLACENM,
        String SVCURL,
        String X,
        String Y,
        String AREANM,
        String IMGURL,
        String DTLCONT,
        String TELNO,
        String V_MIN,
        String V_MAX
) {
}