package com.project.tennis.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tennis.domain.auth.service.AuthTokenService;
import com.project.tennis.domain.member.member.dto.response.AuthTokenResponse;
import com.project.tennis.global.config.SecurityConfig;
import com.project.tennis.global.exception.jwt.JwtAccessDeniedHandler;
import com.project.tennis.global.exception.jwt.JwtAuthenticationEntryPoint;
import com.project.tennis.global.util.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(AuthTokenController.class)
@Import(SecurityConfig.class)
public class AuthTokenControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthTokenService authTokenService;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissueToken_success() throws Exception {
        // given
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";

        AuthTokenResponse responseDto = new AuthTokenResponse(newAccessToken);

        given(authTokenService.reissueAccessToken(eq(refreshToken), any(HttpServletResponse.class)))
                .willReturn(responseDto);

        // when & then
        mvc.perform(post("/api/tokens")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").value(newAccessToken));
    }
}
