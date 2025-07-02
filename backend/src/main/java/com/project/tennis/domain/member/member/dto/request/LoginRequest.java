package com.project.tennis.domain.member.member.dto.request;

public record LoginRequest(
        String identifier,
        String password
) {
}
