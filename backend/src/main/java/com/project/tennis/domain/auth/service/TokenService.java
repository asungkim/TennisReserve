package com.project.tennis.domain.auth.service;

import com.project.tennis.domain.auth.repository.RefreshTokenRepository;
import com.project.tennis.domain.member.member.dto.response.TokenResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
import com.project.tennis.global.util.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${custom.jwt.access_token.expiration_minutes}")
    private long accessTokenExpirationMinutes;

    @Value("${custom.jwt.refresh_token.expiration_minutes}")
    private long refreshTokenExpirationMinutes;

    @Value("${custom.http.secure}")
    private boolean secure;

    public TokenResponse createAccessToken(String refreshToken, HttpServletResponse response) {
        // 1. refreshToken이 유효한지 체크
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new BusinessException(RsCode.UNAUTHENTICATED);
        }

        // 2. refreshToken으로 부터 멤버 정보 가져옴
        String memberId = jwtProvider.getMemberId(refreshToken);

        // 3. 멤버 정보를 통해 redis에 저장된 refreshToken을 검증
        String savedRefreshToken = refreshTokenRepository.findByMemberId(memberId);
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new BusinessException(RsCode.UNAUTHENTICATED);
        }

        // 4. 액세스 토큰 갱신
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        // 5. 리프레시 토큰 갱신 및 쿠키 재발급
        createRefreshToken(member, response);

        return new TokenResponse(generateAccessToken(member));
    }

    public void createRefreshToken(Member member, HttpServletResponse response) {
        String newRefreshToken = generateRefreshToken(member);
        refreshTokenRepository.save(member.getId(), newRefreshToken, Duration.ofMinutes(refreshTokenExpirationMinutes));
        addRefreshTokenCookie(response, newRefreshToken);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String newRefreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofMinutes(refreshTokenExpirationMinutes))
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }

    private String generateRefreshToken(Member member) {
        return jwtProvider.createToken(member.getId(), Duration.ofMinutes(refreshTokenExpirationMinutes), generateClaims(member));
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
