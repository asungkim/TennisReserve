package com.project.tennis.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Document(
        @JsonProperty("road_address")
        RoadAddress roadAddress,  // null일 수 있음
        Address address
) {
}
