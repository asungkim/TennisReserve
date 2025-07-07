package com.project.tennis.domain.auth.service;

import com.project.tennis.domain.member.member.dto.response.TokenResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.global.util.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final JwtProvider jwtProvider;

    public void generateRefreshToken(Member member) {

    }

    public String generateAccessToken(Member member) {
        return null;
    }

    public TokenResponse createAccessToken(String refreshToken, HttpServletResponse response) {
        return null;
    }
}
