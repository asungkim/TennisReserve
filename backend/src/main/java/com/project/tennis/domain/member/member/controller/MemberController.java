package com.project.tennis.domain.member.member.controller;

import com.project.tennis.domain.member.member.dto.request.LoginRequest;
import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.AuthTokenResponse;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.service.MemberService;
import com.project.tennis.global.response.RsData;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.project.tennis.global.response.RsCode.CREATED;
import static com.project.tennis.global.response.RsCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<MemberCreateResponse> createMember(@RequestBody @Valid MemberCreateRequest request) {
        return RsData.from(CREATED, memberService.createMember(request));
    }

    @PostMapping("/login")
    public RsData<AuthTokenResponse> loginMember(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        return RsData.from(SUCCESS, memberService.loginMember(request, response));
    }
}
