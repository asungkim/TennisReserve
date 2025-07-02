package com.project.tennis.domain.member.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.entity.enums.Role;
import com.project.tennis.domain.member.member.service.MemberService;
import com.project.tennis.global.config.JpaAuditingConfig;
import com.project.tennis.global.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

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

    @Autowired
    private ObjectMapper mapper;

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
        ResultActions result = mvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(request.email()))
                .andExpect(jsonPath("$.data.username").value(request.username()))
                .andExpect(jsonPath("$.data.nickname").value(request.nickname()))
                .andExpect(jsonPath("$.data.role").value(Role.USER.name()));

    }
}