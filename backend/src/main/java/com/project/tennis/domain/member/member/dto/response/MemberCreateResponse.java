package com.project.tennis.domain.member.member.dto.response;

import com.project.tennis.domain.member.member.entity.Member;

import java.time.LocalDateTime;

public record MemberCreateResponse(
        String id,
        String username,
        String email,
        String nickname,
        String role,
        LocalDateTime createdAt
) {
    public static MemberCreateResponse toEntity(Member member) {
        return new MemberCreateResponse(
                member.getId(),
                member.getUsername(),
                member.getEmail(),
                member.getNickname(),
                member.getRole().toString(),
                member.getCreatedAt()
        );
    }
}
