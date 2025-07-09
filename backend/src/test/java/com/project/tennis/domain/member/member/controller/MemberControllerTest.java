package com.project.tennis.domain.member.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tennis.domain.member.member.dto.request.LoginRequest;
import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.AuthTokenResponse;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.entity.enums.Role;
import com.project.tennis.domain.member.member.entity.enums.SocialProvider;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.domain.member.member.service.MemberService;
import com.project.tennis.global.config.JpaAuditingConfig;
import com.project.tennis.global.config.SecurityConfig;
import com.project.tennis.global.exception.jwt.JwtAccessDeniedHandler;
import com.project.tennis.global.exception.jwt.JwtAuthenticationEntryPoint;
import com.project.tennis.global.util.jwt.JwtProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(
        controllers = MemberController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JpaAuditingConfig.class)
)
@Import(SecurityConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;


    @Nested
    class SignupTest {
        @Test
        @DisplayName("회원가입 성공")
        void create_success() throws Exception {
            // given
            MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "!password1", "nickname");

            MemberCreateResponse response = new MemberCreateResponse(
                    "123",
                    "user",
                    "email@test.com",
                    "nickname",
                    Role.USER.name(),
                    LocalDateTime.now()
            );

            given(memberService.createMember(any())).willReturn(response);


            // when & then
            ResultActions result = mvc.perform(post("/api/members/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value(request.email()))
                    .andExpect(jsonPath("$.data.username").value(request.username()))
                    .andExpect(jsonPath("$.data.nickname").value(request.nickname()))
                    .andExpect(jsonPath("$.data.role").value(Role.USER.name()));

        }

        @Nested
        class ValidFailTests {

            @Test
            @DisplayName("회원가입 - 모든 필드가 비어있을 경우 NotBlank 유효성 검증 실패")
            void allFieldsBlank() throws Exception {
                MemberCreateRequest request = new MemberCreateRequest("", "", "", "");

                ResultActions result = mvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)));

                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("아이디는 필수")))
                        .andExpect(jsonPath("$.message", containsString("이메일은 필수")))
                        .andExpect(jsonPath("$.message", containsString("비밀번호는 필수")))
                        .andExpect(jsonPath("$.message", containsString("닉네임은 필수")));
            }

            @Test
            @DisplayName("아이디 유효성 검사 실패")
            void invalidUsername() throws Exception {
                MemberCreateRequest request = new MemberCreateRequest("유저!", "email@test.com", "!password1", "nickname");

                ResultActions result = mvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)));

                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("아이디는 4~20자 소문자+숫자만 허용됩니다.")));
            }

            @Test
            @DisplayName("이메일 유효성 검사 실패")
            void invalidEmail() throws Exception {
                MemberCreateRequest request = new MemberCreateRequest("user", "invalid-email", "!password1", "nickname");

                ResultActions result = mvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)));

                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("올바른 이메일 형식")));
            }

            @Test
            @DisplayName("비밀번호 유효성 검사 실패")
            void invalidPassword() throws Exception {
                MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "abc123", "nickname");

                ResultActions result = mvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)));

                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("비밀번호는 8~20자 영문, 숫자, 특수문자를 포함해야 합니다.")));
            }

            @Test
            @DisplayName("닉네임 유효성 검사 실패")
            void invalidNickname() throws Exception {
                MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "!password1", "닉네!임");

                ResultActions result = mvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)));

                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message", containsString("닉네임은 2~20자 한글, 영문, 숫자만 허용됩니다.")));
            }
        }
    }

    @Nested
    class LoginTest {

        @BeforeEach
        void setUp() {
            Member member = Member.builder()
                    .id(UUID.randomUUID().toString())
                    .email("email@test.com")
                    .username("user1")
                    .nickname("nickname")
                    .role(Role.USER)
                    .provider(SocialProvider.NONE)
                    .password(passwordEncoder.encode("!password1"))
                    .build();

            memberRepository.save(member);
        }

        @AfterEach
        void deleteAll() {
            memberRepository.deleteAll();
        }

        @Test
        @DisplayName("로그인 성공")
        void login_success() throws Exception {

            // given
            LoginRequest loginRequest = new LoginRequest("user1", "!password!");
            AuthTokenResponse response = new AuthTokenResponse("mockAccessToken");

            given(memberService.loginMember(any(), any())).willReturn(response);

            // when
            ResultActions result = mvc.perform(post("/api/members/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").value("mockAccessToken"));
        }

        @Test
        @DisplayName("로그인 실패 - @NotBlank")
        void login_fail() throws Exception {

            // given
            LoginRequest loginRequest = new LoginRequest("", "");

            // when
            ResultActions result = mvc.perform(post("/api/members/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(loginRequest)));

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("아이디 또는 이메일은 필수입니다.")))
                    .andExpect(jsonPath("$.message").value(containsString("비밀번호는 필수입니다.")));
        }
    }


}