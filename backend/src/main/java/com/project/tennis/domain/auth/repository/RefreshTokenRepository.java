package com.project.tennis.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token";

    public void save(String memberId, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue().set(getRefreshTokenKey(memberId), refreshToken, expiration);
    }

    public String findByMemberId(String memberId) {
        return redisTemplate.opsForValue().get(getRefreshTokenKey(memberId));
    }

    public void deleteByMemberId(String memberId) {
        redisTemplate.delete(getRefreshTokenKey(memberId));
    }

    private String getRefreshTokenKey(String memberId) {
        return REFRESH_TOKEN_PREFIX + ":memberId:" + memberId;
    }
}
