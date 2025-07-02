package com.project.tennis.domain.auth.service;

import com.project.tennis.domain.member.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    public void generateRefreshToken(Member member) {

    }

    public String generateAccessToken(Member member) {
        return null;
    }
}
