package com.project.tennis.domain.auth.service;

import com.project.tennis.domain.member.member.dto.response.TokenResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
import com.project.tennis.global.util.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Value("${custom.jwt.access_token.expiration_minutes}")
    private long accessTokenExpirationMinutes;

    public TokenResponse createAccessToken(String refreshToken, HttpServletResponse response) {
        // 1. refreshToken이 유효한지 체크
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new BusinessException(RsCode.UNAUTHENTICATED);
        }

        // 2. refreshToken으로 부터 멤버 정보 가져옴
        String memberId = jwtProvider.getMemberId(refreshToken);

        // 3. 멤버 정보를 통해 redis에 저장된 refreshToken을 검증

        // 4. refreshToken 갱신

        // 5. 액세스 토큰 갱신
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        return new TokenResponse(generateAccessToken(member));
    }

    public void generateRefreshToken(Member member) {

    }

    public String generateAccessToken(Member member) {
        return jwtProvider.createToken(member.getId(), Duration.ofMinutes(accessTokenExpirationMinutes), generateClaims(member));
    }

    private Map<String, Object> generateClaims(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("role", member.getRole());

        return claims;
    }
}
