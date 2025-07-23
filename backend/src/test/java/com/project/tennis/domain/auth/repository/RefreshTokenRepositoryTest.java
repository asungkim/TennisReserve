package com.project.tennis.domain.auth.repository;

import com.project.tennis.domain.auth.token.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("Redis Token CRUD 성공")
    void saveAndFindAndDelete() {
        String memberId = "member" + UUID.randomUUID();
        String token = "refreshToken";
        Duration expiration = Duration.ofMinutes(30);

        refreshTokenRepository.save(memberId, token, expiration);

        String result = refreshTokenRepository.findByMemberId(memberId);
        assertThat(result).isEqualTo(token);

        refreshTokenRepository.deleteByMemberId(memberId);

        String deleted = refreshTokenRepository.findByMemberId(memberId);
        assertThat(deleted).isNull();
    }
}