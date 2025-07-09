package com.project.tennis.domain.auth.service;

import com.project.tennis.domain.auth.repository.RefreshTokenRepository;
import com.project.tennis.domain.member.member.dto.response.AuthTokenResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.entity.enums.Role;
import com.project.tennis.domain.member.member.entity.enums.SocialProvider;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
import com.project.tennis.global.util.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @InjectMocks
    private AuthTokenService authTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;


    @Nested
    @DisplayName("액세스 토큰 재발급 관련 테스트")
    class ReissueToken {

        @Test
        @DisplayName("액세스 토큰 재발급 성공")
        void reissue_success() {
            // given
            String refreshToken = "valid-refresh-token";
            String memberId = UUID.randomUUID().toString();
            String newAccessToken = "new-access-token";

            Member member = Member.builder()
                    .id(memberId)
                    .email("email@test.com")
                    .username("username")
                    .nickname("nickname")
                    .role(Role.USER)
                    .provider(SocialProvider.NONE)
                    .password("encode-pw")
                    .build();

            MockHttpServletResponse response = new MockHttpServletResponse();

            given(jwtProvider.isValidToken(refreshToken)).willReturn(true);
            given(jwtProvider.getMemberId(refreshToken)).willReturn(memberId);
            given(refreshTokenRepository.findByMemberId(memberId)).willReturn(refreshToken);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
            given(jwtProvider.createToken(eq(memberId), any(), any())).willReturn(newAccessToken); // accessTok

            // createRefreshToken 는 별도로 테스트
            AuthTokenService spyService = Mockito.spy(authTokenService);
            doNothing().when(spyService).createRefreshToken(eq(member), eq(response));

            // when
            AuthTokenResponse tokenResponse = spyService.reissueAccessToken(refreshToken, response);

            // then
            assertThat(tokenResponse.accessToken()).isEqualTo(newAccessToken);
            verify(spyService).createRefreshToken(eq(member), any());
        }

        @Test
        @DisplayName("재발급 실패 - 만료된 리프레시 토큰")
        void reissue_fail_invalid_refresh_token() {
            // given
            String invalidRefreshToken = "invalid-token";
            HttpServletResponse response = new MockHttpServletResponse();

            given(jwtProvider.isValidToken(invalidRefreshToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authTokenService.reissueAccessToken(invalidRefreshToken, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(RsCode.UNAUTHENTICATED.getMessage());
        }

        @Test
        @DisplayName("재발급 실패 - Redis 저장된 토큰과 불일치(탈취)")
        void reissue_fail_token_mismatch() {
            // given
            String tokenFromClient = "token-A";
            String memberId = "member-123";
            HttpServletResponse response = new MockHttpServletResponse();

            given(jwtProvider.isValidToken(tokenFromClient)).willReturn(true);
            given(jwtProvider.getMemberId(tokenFromClient)).willReturn(memberId);
            given(refreshTokenRepository.findByMemberId(memberId)).willReturn("token-B"); // Redis에는 다른 값

            // when & then
            assertThatThrownBy(() -> authTokenService.reissueAccessToken(tokenFromClient, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(RsCode.UNAUTHENTICATED.getMessage());
        }

        @Test
        @DisplayName("재발급 실패 - 멤버 없음")
        void reissue_fail_member_not_found() {
            // given
            String refreshToken = "valid-token";
            String memberId = "member-123";
            HttpServletResponse response = new MockHttpServletResponse();

            given(jwtProvider.isValidToken(refreshToken)).willReturn(true);
            given(jwtProvider.getMemberId(refreshToken)).willReturn(memberId);
            given(refreshTokenRepository.findByMemberId(memberId)).willReturn(refreshToken);
            given(memberRepository.findById(memberId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authTokenService.reissueAccessToken(refreshToken, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(RsCode.NOT_FOUND.getMessage());
        }
    }


    @Test
    @DisplayName("리프레시 토큰 발급 및 쿠키 설정 성공")
    void createRefreshToken_success() {
        // given
        String memberId = UUID.randomUUID().toString();
        Member member = Member.builder()
                .id(memberId)
                .email("email@test.com")
                .username("username")
                .nickname("nickname")
                .role(Role.USER)
                .provider(SocialProvider.NONE)
                .password("encode-pw")
                .build();

        String mockRefreshToken = "mock-refresh-token";
        MockHttpServletResponse response = new MockHttpServletResponse();

        given(jwtProvider.createToken(eq(memberId), any(), any())).willReturn(mockRefreshToken);

        // when
        authTokenService.createRefreshToken(member, response);

        // then
        verify(refreshTokenRepository).save(eq(memberId), eq(mockRefreshToken), any());
        String cookieValue = response.getHeader("Set-Cookie");
        assertThat(cookieValue).contains("refreshToken=" + mockRefreshToken);
    }
}