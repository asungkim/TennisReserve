package com.project.tennis.domain.member.member.service;

import com.project.tennis.domain.auth.service.AuthTokenService;
import com.project.tennis.domain.member.member.dto.request.LoginRequest;
import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.dto.response.AuthTokenResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.entity.enums.Role;
import com.project.tennis.domain.member.member.entity.enums.SocialProvider;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.global.exception.BusinessException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthTokenService authTokenService;

    @Nested
    @DisplayName("회원가입 관련 테스트")
    class SignupTest {
        @Test
        @DisplayName("회원가입 성공")
        void create_success() {
            // 1. given
            MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

            // 값 지정
            given(memberRepository.existsByEmail(request.email())).willReturn(false);
            given(memberRepository.existsByUsername(request.username())).willReturn(false);
            given(memberRepository.existsByNickname(request.nickname())).willReturn(false);
            given(passwordEncoder.encode(request.password())).willReturn("encode-pw");

            // 저장될 Member 모의 반환
            Member savedMember = Member.builder()
                    .id(UUID.randomUUID().toString())
                    .email(request.email())
                    .username(request.username())
                    .nickname(request.nickname())
                    .role(Role.USER)
                    .provider(SocialProvider.NONE)
                    .password("encode-pw")
                    .build();

            given(memberRepository.save(any(Member.class))).willReturn(savedMember);

            // when
            MemberCreateResponse response = memberService.createMember(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isNotNull();
            assertThat(response.email()).isEqualTo(request.email());
            assertThat(response.username()).isEqualTo(request.username());
            assertThat(response.nickname()).isEqualTo(request.nickname());
            assertThat(response.role()).isEqualTo(Role.USER.name());
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        void create_fail_duplicate_email() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

            given(memberRepository.existsByEmail(request.email())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("이미 사용 중인 이메일입니다."); // 예외 메시지는 실제 사용한 메시지에 따라 수정
        }

        @DisplayName("회원가입 실패 - 아이디 중복")
        @Test
        void create_fail_duplicate_username() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

            given(memberRepository.existsByUsername(request.username())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("이미 사용 중인 아이디입니다.");
        }

        @DisplayName("회원가입 실패 - 닉네임 중복")
        @Test
        void create_fail_duplicate_nickname() {
            // given
            MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

            given(memberRepository.existsByEmail(request.email())).willReturn(false);
            given(memberRepository.existsByUsername(request.username())).willReturn(false);
            given(memberRepository.existsByNickname(request.nickname())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("이미 사용 중인 닉네임입니다.");
        }
    }

    @Nested
    @DisplayName("로그인 관련 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공 - 이메일 ")
        void login_success_email() {
            // given 1 : 멤버 준비
            LoginRequest emailRequest = new LoginRequest("email@test.com", "!password!");



            Member member = Member.builder()
                    .id(UUID.randomUUID().toString())
                    .email("email@test.com")
                    .username("username")
                    .nickname("nickname")
                    .role(Role.USER)
                    .provider(SocialProvider.NONE)
                    .password("encode-pw")
                    .build();

            given(memberRepository.findByEmail(emailRequest.identifier())).willReturn(Optional.of(member));
            given(passwordEncoder.matches("!password!", "encode-pw")).willReturn(true);

            // given 2 : 토큰 및 쿠키 준비
            given(authTokenService.generateAccessToken(member)).willReturn("mockAccessToken");
            doAnswer(invocation -> {
                HttpServletResponse resp = invocation.getArgument(1, HttpServletResponse.class);
                Cookie cookie = new Cookie("refreshToken", "mockRefreshToken");
                resp.addCookie(cookie);
                return null;
            }).when(authTokenService).createRefreshToken(eq(member), any(HttpServletResponse.class));

            MockHttpServletResponse response = new MockHttpServletResponse();

            // when
            AuthTokenResponse authTokenResponse = memberService.loginMember(emailRequest, response);

            // then
            assertThat(authTokenResponse.accessToken()).isEqualTo("mockAccessToken");
            assertThat(response.getCookies()).isNotEmpty();
            assertThat(response.getCookie("refreshToken").getValue()).isEqualTo("mockRefreshToken");
        }

        @Test
        @DisplayName("로그인 성공 - 아이디 ")
        void login_success_username() {
            // given 1 : 멤버 준비
            LoginRequest idRequest = new LoginRequest("username", "!password!");


            Member member = Member.builder()
                    .id(UUID.randomUUID().toString())
                    .email("email@test.com")
                    .username("username")
                    .nickname("nickname")
                    .role(Role.USER)
                    .provider(SocialProvider.NONE)
                    .password("encode-pw")
                    .build();

            given(memberRepository.findByUsername(idRequest.identifier())).willReturn(Optional.of(member));
            given(passwordEncoder.matches("!password!", "encode-pw")).willReturn(true);

            // given 2 : 토큰 및 쿠키 준비
            given(authTokenService.generateAccessToken(member)).willReturn("mockAccessToken");
            doAnswer(invocation -> {
                HttpServletResponse resp = invocation.getArgument(1, HttpServletResponse.class);
                Cookie cookie = new Cookie("refreshToken", "mockRefreshToken");
                resp.addCookie(cookie);
                return null;
            }).when(authTokenService).createRefreshToken(eq(member), any(HttpServletResponse.class));

            MockHttpServletResponse response = new MockHttpServletResponse();

            // when
            AuthTokenResponse authTokenResponse = memberService.loginMember(idRequest, response);

            // then
            assertThat(authTokenResponse.accessToken()).isEqualTo("mockAccessToken");
            assertThat(response.getCookies()).isNotEmpty();
            assertThat(response.getCookie("refreshToken").getValue()).isEqualTo("mockRefreshToken");
        }

        @Test
        @DisplayName("로그인 실패 - 존재하지 않는 이메일")
        void login_fail_email_not_exist() {
            // given
            LoginRequest request = new LoginRequest("noone@test.com", "!password1");

            given(memberRepository.findByEmail(request.identifier())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.loginMember(request, new MockHttpServletResponse()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("존재하지 않는 이메일입니다.");
        }

        @Test
        @DisplayName("로그인 실패 - 존재하지 않는 아이디")
        void login_fail_username_not_exist() {
            // given
            LoginRequest request = new LoginRequest("unknownuser", "password");

            given(memberRepository.findByUsername(request.identifier())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.loginMember(request, new MockHttpServletResponse()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("존재하지 않는 아이디입니다.");
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호 불일치")
        void login_fail_password_not_match() {
            // given
            LoginRequest request = new LoginRequest("email@test.com", "wrongpassword");

            Member member = Member.builder()
                    .id(UUID.randomUUID().toString())
                    .email("email@test.com")
                    .username("username")
                    .nickname("nickname")
                    .role(Role.USER)
                    .provider(SocialProvider.NONE)
                    .password("encode-pw")
                    .build();

            given(memberRepository.findByEmail(request.identifier())).willReturn(Optional.of(member));
            given(passwordEncoder.matches("wrongpassword", "encode-pw")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> memberService.loginMember(request, new MockHttpServletResponse()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("비밀번호가 일치하지 않습니다.");
        }
    }
}