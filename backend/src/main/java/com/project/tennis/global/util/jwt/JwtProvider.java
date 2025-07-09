package com.project.tennis.global.util.jwt;

import com.project.tennis.domain.member.member.dto.MemberPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final static String TOKEN_PREFIX = "Bearer ";
    private final JwtProperties jwtProperties;

    // JWT 토큰 생성
    public String createToken(String subject, Duration expiredAt, Map<String, Object> claims) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiredAt.toMillis());

        Header jwtHeader = Jwts.header().type("JWT").build();
        SecretKey key = getSecretKey();

        JwtBuilder builder = Jwts.builder()
                .header().add(jwtHeader).and()
                .signWith(key, Jwts.SIG.HS256)
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expireDate);

        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        return builder.compact();
    }

    // 토큰 검증
    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Claims 내용 가져오기
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String getMemberId(String refreshToken) {
        return (String) getClaims(refreshToken).get("id");
    }

    public String getTokenFromHeader(String header) {
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        return header.substring(TOKEN_PREFIX.length());
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String role = claims.get("role", String.class);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);

        return new UsernamePasswordAuthenticationToken(
                new MemberPrincipal(getMemberId(token)),
                null,
                List.of(grantedAuthority)
        );
    }
}
