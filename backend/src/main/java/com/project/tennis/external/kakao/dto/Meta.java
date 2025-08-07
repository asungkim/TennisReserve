package com.project.tennis.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Meta(
        @JsonProperty("total_count")
        int totalCount
) {
}
